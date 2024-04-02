package com.example.and103_thanghtph31577_lab5.view;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.example.and103_thanghtph31577_lab5.model.Distributor;
import com.example.and103_thanghtph31577_lab5.model.Fruit;
import com.example.and103_thanghtph31577_lab5.model.Response;
import com.example.and103_thanghtph31577_lab5.services.HttpRequest;
import com.google.android.material.snackbar.Snackbar;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.and103_thanghtph31577_lab5.databinding.ActivityUpdateBinding;

import com.example.and103_thanghtph31577_lab5.R;

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

public class UpdateActivity extends AppCompatActivity {

    private ActivityUpdateBinding binding;

    private HttpRequest httpRequest;
    private String id_Distributor;
    private ArrayList<Distributor> distributorArrayList;
    private ArrayList<File> ds_image;

    private Fruit fruit;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityUpdateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ds_image = new ArrayList<>();
        httpRequest = new HttpRequest();
        fruit = new Fruit();
        getDataIntent();
        configSpinner();
        userListener();

    }

    private void getDataIntent() {
        //get data object intent
        Intent intent = getIntent();
        fruit = (Fruit) intent.getSerializableExtra("fruit");
        id = fruit.get_id();
        String url = fruit.getImage().get(0);
        String newUrl = url.replace("localhost", "10.0.0.2");
        Glide.with(this)
                .load(newUrl)
                .thumbnail(Glide.with(this).load(R.drawable.baseline_broken_image_24))
                .into(binding.avatar);
        binding.edName.setText(fruit.getName());
        binding.edPrice.setText(fruit.getPrice());
        binding.edQuantity.setText(fruit.getQuantity());
        binding.edDescription.setText(fruit.getDescription());
        binding.edStatus.setText(fruit.getStatus());

        createFileFormUri(Uri.parse(newUrl), "image");


        // Convert URL to File
        Uri uri = Uri.parse(newUrl);
        File file = createFileFormUri(uri, "image");
        if (file != null) {
            ds_image.add(file);
        }

    }

    private void userListener() {
        binding.avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        binding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, RequestBody> mapRequestBody = new HashMap<>();
                String _name = binding.edName.getText().toString().trim();
                String _quantity = binding.edQuantity.getText().toString().trim();
                String _price = binding.edPrice.getText().toString().trim();
                String _status = binding.edStatus.getText().toString().trim();
                String _description = binding.edDescription.getText().toString().trim();

                mapRequestBody.put("name", getRequestBody(_name));
                mapRequestBody.put("quantity", getRequestBody(_quantity));
                mapRequestBody.put("price", getRequestBody(_price));
                mapRequestBody.put("status", getRequestBody(_status));
                mapRequestBody.put("description", getRequestBody(_description));
                mapRequestBody.put("id_distributor", getRequestBody(id_Distributor));
                ArrayList<MultipartBody.Part> _ds_image = new ArrayList<>();
//
                // Kiểm tra xem người dùng đã chọn ảnh mới hay không
                if (ds_image.isEmpty()) {
                    // Nếu không có ảnh mới, thêm các ảnh cũ vào danh sách
                    for (String imagePath : fruit.getImage()) {
                        File imageFile = new File(imagePath);
                        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), imageFile);
                        MultipartBody.Part multipartBodyPart = MultipartBody.Part.createFormData("image", imageFile.getName(), requestFile);
                        _ds_image.add(multipartBodyPart);
                    }
                } else {
                    // Nếu có ảnh mới, thêm các ảnh mới vào danh sách
                    ds_image.forEach(file1 -> {
                        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file1);
                        MultipartBody.Part multipartBodyPart = MultipartBody.Part.createFormData("image", file1.getName(), requestFile);
                        _ds_image.add(multipartBodyPart);
                    });
                }

                // Gửi yêu cầu cập nhật lên server
                httpRequest.callAPI().updateFruitWithFileImage(mapRequestBody,
                        fruit.get_id(), _ds_image).enqueue(responseFruit);


            }
        });
    }

    Callback<Response<Fruit>> responseFruit = new Callback<Response<Fruit>>() {
        @Override
        public void onResponse(Call<Response<Fruit>> call, retrofit2.Response<Response<Fruit>> response) {
            if (response.isSuccessful()) {
                Log.d("123123", "onResponse: " + response.body().getStatus());
                if (response.body().getStatus() == 200) {
                    Toast.makeText(UpdateActivity.this, "Updatethành công", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }
            }
        }

        @Override
        public void onFailure(Call<Response<Fruit>> call, Throwable t) {
            Toast.makeText(UpdateActivity.this, "Update không thành công", Toast.LENGTH_SHORT).show();
            onBackPressed();
            Log.e("zzzzzzzzzz", "onFailure: " + t.getMessage());
        }
    };

    private RequestBody getRequestBody(String value) {
        return RequestBody.create(MediaType.parse("multipart/form-data"), value);
    }

    private void chooseImage() {
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

                        if (tempUri != null) {
                            Glide.with(UpdateActivity.this)
                                    .load(tempUri)
                                    .thumbnail(Glide.with(UpdateActivity.this).load(R.drawable.baseline_broken_image_24))
                                    .centerCrop()
                                    .circleCrop()
                                    .skipMemoryCache(true)
//                                .diskCacheStrategy(DiskCacheStrategy.NONE)
//                                .skipMemoryCache(true)
                                    .into(binding.avatar);
                        }

                    }
                }
            });

    private File createFileFormUri(Uri path, String name) {
        File _file = new File(UpdateActivity.this.getCacheDir(), name + ".png");
        try {
            InputStream in = UpdateActivity.this.getContentResolver().openInputStream(path);
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

    private void configSpinner() {
        httpRequest.callAPI().getListDistributor().enqueue(getDistributorAPI);
        binding.spDistributor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                id_Distributor = distributorArrayList.get(position).getId();
                Log.d("123123", "onItemSelected: " + id_Distributor);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        binding.spDistributor.setSelection(0);
    }

    Callback<Response<ArrayList<Distributor>>> getDistributorAPI = new Callback<Response<ArrayList<Distributor>>>() {
        @Override
        public void onResponse(Call<Response<ArrayList<Distributor>>> call, retrofit2.Response<Response<ArrayList<Distributor>>> response) {
            if (response.isSuccessful()) {
                if (response.body().getStatus() == 200) {
                    distributorArrayList = response.body().getData();
                    String[] items = new String[distributorArrayList.size()];

                    for (int i = 0; i < distributorArrayList.size(); i++) {
                        items[i] = distributorArrayList.get(i).getName();
                    }
                    ArrayAdapter<String> adapterSpin = new ArrayAdapter<>(UpdateActivity.this, android.R.layout.simple_spinner_item, items);
                    adapterSpin.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.spDistributor.setAdapter(adapterSpin);
                }
            }
        }

        @Override
        public void onFailure(Call<Response<ArrayList<Distributor>>> call, Throwable t) {
            t.getMessage();
        }

    };


}