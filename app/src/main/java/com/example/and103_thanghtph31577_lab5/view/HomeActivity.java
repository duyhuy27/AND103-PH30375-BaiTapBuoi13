package com.example.and103_thanghtph31577_lab5.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.and103_thanghtph31577_lab5.MainActivity;
import com.example.and103_thanghtph31577_lab5.R;
import com.example.and103_thanghtph31577_lab5.adapter.FruitAdapter;
import com.example.and103_thanghtph31577_lab5.databinding.ActivityHomeBinding;
import com.example.and103_thanghtph31577_lab5.databinding.DialogAddBinding;
import com.example.and103_thanghtph31577_lab5.databinding.DialogUpdateFrBinding;
import com.example.and103_thanghtph31577_lab5.model.Distributor;
import com.example.and103_thanghtph31577_lab5.model.Fruit;
import com.example.and103_thanghtph31577_lab5.model.Response;
import com.example.and103_thanghtph31577_lab5.services.HttpRequest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;

public class HomeActivity extends AppCompatActivity implements FruitAdapter.FruitClick {
    ActivityHomeBinding binding;
    private HttpRequest httpRequest;
    private SharedPreferences sharedPreferences;
    private String token;
    private FruitAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());
        ds_image = new ArrayList<>();
        httpRequest = new HttpRequest();
        sharedPreferences = getSharedPreferences("INFO", MODE_PRIVATE);

        token = sharedPreferences.getString("token", "");
        httpRequest.callAPI().getListFruit("Bearer " + token).enqueue(getListFruitResponse);
        userListener();
    }

    private void userListener() {
        binding.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, AddFruitActivity.class));
            }
        });

        binding.nextMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, MainActivity.class));

            }
        });
    }


    Callback<Response<ArrayList<Fruit>>> getListFruitResponse = new Callback<Response<ArrayList<Fruit>>>() {
        @Override
        public void onResponse(Call<Response<ArrayList<Fruit>>> call, retrofit2.Response<Response<ArrayList<Fruit>>> response) {
            if (response.isSuccessful()) {
                if (response.body().getStatus() == 200) {
                    ArrayList<Fruit> ds = response.body().getData();
                    getData(ds);
//                    Toast.makeText(HomeActivity.this, response.body().getMessenger(), Toast.LENGTH_SHORT).show();
                }
            }
        }

        @Override
        public void onFailure(Call<Response<ArrayList<Fruit>>> call, Throwable t) {

        }
    };

    private void getData(ArrayList<Fruit> ds) {
        adapter = new FruitAdapter(this, ds, this);
        binding.rcvFruit.setAdapter(adapter);
    }
//    Callback<Response<Fruit>> responseDistributorAPI  = new Callback<Response<Fruit>>() {
//        @Override
//        public void onResponse(Call<Response<Fruit>> call, retrofit2.Response<Response<Fruit>> response) {
//            if (response.isSuccessful()) {
//                if (response.body().getStatus() == 200) {
//                    httpRequest.callAPI()
//                            .getListDistributor()
//                            .enqueue(getListFruitResponse);
//                    Toast.makeText(HomeActivity.this, response.body().getMessenger(), Toast.LENGTH_SHORT).show();
//                }
//            }
//        }
//
//        @Override
//        public void onFailure(Call<Response<Fruit>> call, Throwable t) {
//            Log.e("zzzz", "onFailure: "+t.getMessage() );
//        }
//    };

    @Override
    public void delete(Fruit fruit) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm delete");
        builder.setMessage("Are you sure you want to delete?");
        builder.setPositiveButton("yes", (dialog, which) -> {
            httpRequest.callAPI()
                    .deleteFruits(fruit.get_id()) // Corrected to deleteFruit API
                    .enqueue(new Callback<Response<Distributor>>() {
                        @Override
                        public void onResponse(Call<Response<Distributor>> call, retrofit2.Response<Response<Distributor>> response) {
                            if (response.isSuccessful()) {
                                if (response.body().getStatus() == 200) {
                                    httpRequest.callAPI().getListFruit("Bearer " + token).enqueue(getListFruitResponse);
                                    Toast.makeText(HomeActivity.this, response.body().getMessenger(), Toast.LENGTH_SHORT).show();
                                }
                            }

                        }

                        @Override
                        public void onFailure(Call<Response<Distributor>> call, Throwable t) {
                            Log.e("zzzz", "onFailure: " + t.getMessage());
                            Toast.makeText(HomeActivity.this, "Failed to delete", Toast.LENGTH_SHORT).show();

                        }
                    });
        });
        builder.setNegativeButton("no", (dialog, which) -> {
            dialog.dismiss();
        });
        builder.show();
    }


    private String id_Distributor;

    @Override
    public void edit(Fruit fruit) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Add distributor");
//        DialogUpdateFrBinding binding1 = DialogUpdateFrBinding.inflate(LayoutInflater.from(this));
//        mDialogBinding = binding1;
//        builder.setView(binding1.getRoot());
//        AlertDialog alertDialog = builder.create();
//        String url = fruit.getImage().get(0);
//        String newUrl = url.replace("localhost", "192.168.1.11");
//        Glide.with(this)
//                .load(newUrl)
//                .thumbnail(Glide.with(this).load(R.drawable.baseline_broken_image_24))
//                .into(binding1.avatar);
//        binding1.edName.setText(fruit.getName());
//        binding1.edPrice.setText(fruit.getPrice());
//        binding1.edQuantity.setText(fruit.getQuantity());
//        binding1.edDescription.setText(fruit.getDescription());
//        binding1.edStatus.setText(fruit.getStatus());
//
////spinner distributor
//
//        Callback<Response<ArrayList<Distributor>>> getDistributorAPI = new Callback<Response<ArrayList<Distributor>>>() {
//            @Override
//            public void onResponse(Call<Response<ArrayList<Distributor>>> call, retrofit2.Response<Response<ArrayList<Distributor>>> response) {
//                if (response.isSuccessful()) {
//                    if (response.body().getStatus() == 200) {
//                        distributorArrayList = response.body().getData();
//                        String[] items = new String[distributorArrayList.size()];
//
//                        for (int i = 0; i < distributorArrayList.size(); i++) {
//                            items[i] = distributorArrayList.get(i).getName();
//                        }
//                        ArrayAdapter<String> adapterSpin = new ArrayAdapter<>(HomeActivity.this, android.R.layout.simple_spinner_item, items);
//                        adapterSpin.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                        binding1.spDistributor.setAdapter(adapterSpin);
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Response<ArrayList<Distributor>>> call, Throwable t) {
//                t.getMessage();
//            }
//
//        };
//        httpRequest.callAPI().getListDistributor().enqueue(getDistributorAPI);
//        binding1.spDistributor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                id_Distributor = distributorArrayList.get(position).getId().toString();
//                Log.d("123123", "onItemSelected: " + id_Distributor);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
//        binding1.spDistributor.setSelection(0);
//
//        //avatar choose
//        binding1.avatar.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                chooseImage(binding1.avatar);
//            }
//        });
//
//        binding1.btnCancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                alertDialog.dismiss();
//            }
//        });
//
//        binding1.btnUpdate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Map<String, RequestBody> mapRequestBody = new HashMap<>();
//                String _name = binding1.edName.getText().toString().trim();
//                String _quantity = binding1.edQuantity.getText().toString().trim();
//                String _price = binding1.edPrice.getText().toString().trim();
//                String _status = binding1.edStatus.getText().toString().trim();
//                String _description = binding1.edDescription.getText().toString().trim();
//
//                mapRequestBody.put("name", getRequestBody(_name));
//                mapRequestBody.put("quantity", getRequestBody(_quantity));
//                mapRequestBody.put("price", getRequestBody(_price));
//                mapRequestBody.put("status", getRequestBody(_status));
//                mapRequestBody.put("description", getRequestBody(_description));
//                mapRequestBody.put("id_distributor", getRequestBody("660c31b463b66f131cb80bf1"));
//                Log.d("zzzz", "onClick: id ditributor" + getRequestBody(id_Distributor));
//                Log.d("zzz", "onClick: description" + getRequestBody(_description));
//                ArrayList<MultipartBody.Part> _ds_image = new ArrayList<>();
//
//                // Kiểm tra xem người dùng đã chọn ảnh mới hay không
//                if (ds_image.isEmpty()) {
//                    // Nếu không có ảnh mới, thêm các ảnh cũ vào danh sách
//                    for (String imagePath : fruit.getImage()) {
//                        File imageFile = new File(imagePath);
//                        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), imageFile);
//                        MultipartBody.Part multipartBodyPart = MultipartBody.Part.createFormData("image", imageFile.getName(), requestFile);
//                        _ds_image.add(multipartBodyPart);
//                    }
//                } else {
//                    // Nếu có ảnh mới, thêm các ảnh mới vào danh sách
//                    ds_image.forEach(file1 -> {
//                        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file1);
//                        MultipartBody.Part multipartBodyPart = MultipartBody.Part.createFormData("image", file1.getName(), requestFile);
//                        _ds_image.add(multipartBodyPart);
//                    });
//                }
//
//                // Gửi yêu cầu cập nhật lên server
//                httpRequest.callAPI().updateFruitWithFileImage(mapRequestBody,
//                        fruit.get_id(), _ds_image).enqueue(responseFruit);
//                httpRequest.callAPI().getListFruit("Bearer " + token).enqueue(getListFruitResponse);
//
//                alertDialog.dismiss();
//            }
//        });
//
//
//        alertDialog.show();
        Intent intent = new Intent(HomeActivity.this, UpdateActivity.class);
        intent.putExtra("fruit", fruit);
        startActivity(intent);
    }

    Callback<Response<Fruit>> responseFruit = new Callback<Response<Fruit>>() {
        @Override
        public void onResponse(Call<Response<Fruit>> call, retrofit2.Response<Response<Fruit>> response) {
            if (response.isSuccessful()) {
                Log.d("123123", "onResponse: " + response.body().getStatus());
                if (response.body().getStatus() == 200) {
                    Toast.makeText(HomeActivity.this, "Update thành công", Toast.LENGTH_SHORT).show();
                    httpRequest.callAPI().getListFruit("Bearer " + token).enqueue(getListFruitResponse);

                }
            }
        }

        @Override
        public void onFailure(Call<Response<Fruit>> call, Throwable t) {
            Toast.makeText(HomeActivity.this, "Update không thành công", Toast.LENGTH_SHORT).show();
            Log.e("zzzzzzzzzz", "onFailure: " + t.getMessage());
        }
    };

    private RequestBody getRequestBody(String value) {
        return RequestBody.create(MediaType.parse("multipart/form-data"), value);
    }

    private void chooseImage(ImageView imageView) {
//        if (ContextCompat.checkSelfPermission(RegisterActivity.this,
//                android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
        Log.d("123123", "chooseAvatar: " + 123123);
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        getImage.launch(intent);
//        }else {
//            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
//
//        }
    }

    ActivityResultLauncher<Intent> getImage = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult o) {
                    if (o.getResultCode() == Activity.RESULT_OK) {

                        Uri tempUri = null;

                        ds_image.clear();
                        Intent data = o.getData();
                        if (data.getClipData() != null) {
                            int count = data.getClipData().getItemCount();
                            for (int i = 0; i < count; i++) {
                                Uri imageUri = data.getClipData().getItemAt(i).getUri();
                                tempUri = imageUri;

                                File file = createFileFormUri(imageUri, "image" + i);
                                ds_image.add(file);
                            }


                        } else if (data.getData() != null) {
                            // Trường hợp chỉ chọn một hình ảnh
                            Uri imageUri = data.getData();

                            tempUri = imageUri;
                            // Thực hiện các xử lý với imageUri
                            File file = createFileFormUri(imageUri, "image");
                            ds_image.add(file);

                        }

                        if (tempUri != null && mDialogBinding != null) {
                            Glide.with(mDialogBinding.getRoot().getContext())
                                    .load(tempUri)
                                    .thumbnail(Glide.with(mDialogBinding.getRoot().getContext()).load(R.drawable.baseline_broken_image_24))
                                    .centerCrop()
                                    .circleCrop()
                                    .skipMemoryCache(true)
                                    .into(mDialogBinding.avatar);
                        }


                    }
                }
            });
    private DialogUpdateFrBinding mDialogBinding;

    private File createFileFormUri(Uri path, String name) {
        File _file = new File(HomeActivity.this.getCacheDir(), name + ".png");
        try {
            InputStream in = HomeActivity.this.getContentResolver().openInputStream(path);
            OutputStream out = new FileOutputStream(_file);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.close();
            in.close();
            Log.d("123123", "createFileFormUri: " + _file);
            return _file;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        httpRequest.callAPI().getListFruit("Bearer " + token).enqueue(getListFruitResponse);
    }

    private ArrayList<Distributor> distributorArrayList;
    private ArrayList<File> ds_image;
}