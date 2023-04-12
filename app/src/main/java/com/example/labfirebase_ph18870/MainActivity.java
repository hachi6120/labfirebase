package com.example.labfirebase_ph18870;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    EditText edtTenTruyen, edtGia;
    TextInputLayout tilTenTruyen, tilGia;
    ImageView img;
    RecyclerView recyclerView;
    SearchView searchView;
    private int IMAGE_REQUEST = 1;
    private Uri filePath;

    FirebaseFirestore db;
    FirebaseStorage storage;
    StorageReference storageReference;

    List<Book> objBook  = new ArrayList<>();
    AdapterBook adapterBook;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        edtTenTruyen = findViewById(R.id.truyen_edt_tentruyen);
        edtGia = findViewById(R.id.truyen_edt_gia);

        tilTenTruyen = findViewById(R.id.truyen_til_tentruyen);
        tilGia = findViewById(R.id.truyen_til_gia);

        img = findViewById(R.id.truyen_img);

        recyclerView = findViewById(R.id.truyen_rcv);

        searchView = findViewById(R.id.truyen_search);

        FirebaseApp.initializeApp(this);

        //Storage
        storage = FirebaseStorage.getInstance("gs://labfirebaseph18870.appspot.com");
        storageReference = storage.getReference();

        //Firestore
        db  = FirebaseFirestore.getInstance();

        adapterBook = new AdapterBook();


        ReadBook();

        findViewById(R.id.truyen_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectImage();
            }
        });

        //btn up anh
        findViewById(R.id.truyen_btn_upload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int temp = 0;
                if (filePath == null){
                    Toast.makeText(MainActivity.this, "Chưa chọn ảnh", Toast.LENGTH_SHORT).show();
                    temp++;
                }
                if (edtTenTruyen.getText().toString().isEmpty()){
                    tilTenTruyen.setError("Tên truyện không được để trống");
                    temp++;
                }else {
                    tilGia.setError("");
                }
                if (edtGia.getText().toString().isEmpty()){
                    tilGia.setError("Giá truyện không được để trống");
                    temp++;
                }else {
                    tilGia.setError("");
                }

                if (temp == 0){
                    uploadImage();
                }
            }
        });

        //buttom clear
        findViewById(R.id.truyen_btn_clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtTenTruyen.setText("");
                edtGia.setText("");
                tilTenTruyen.setError("");
                tilGia.setError("");
                img.setImageResource(getResources().getIdentifier("@android:drawable/ic_menu_camera", null, null));
                filePath = null;
            }
        });

        //btn dang xuat
        findViewById(R.id.truyen_dx).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this,LoginActivity.class));
                finish();
            }
        });

        //bo auto focus tro chuot
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapterBook.filter(newText);
                return false;
            }
        });
    }

    private void SelectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image from here..."), IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            Log.d("zzzzz", "onActivityResult: " + filePath.toString());
            try {
                // xem thử ảnh , nếu không muốn xem thử thì bỏ đoạn code này
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                img.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void uploadImage() {
        if (filePath != null) {
            // Hiển thị dialog
         /*   ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Tải ảnh...");
            progressDialog.show();*/
            // Tạo đường dẫn lưu trữ file, images/ là 1 thư mục trên firebase, chuỗi uuid... là tên file, tạm thời có thể phải lên web firebase tạo sẵn thư mục images
            StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());
            Log.d("TAG", "uploadImage: " + ref.getPath());
            // Tiến hành upload file
            ref.putFile(filePath)
                    .addOnSuccessListener(
                            new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    // upload thành công, tắt dialog
                                    /*progressDialog.dismiss();*/

                                }
                            })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            e.printStackTrace(); // có lỗi upload
                           /* progressDialog.dismiss();*/
                            Toast.makeText(MainActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(
                            new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                    // cập nhật tiến trình upload
                                  /*  double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                                    progressDialog.setMessage("Đang thực hiện tải lên " + (int) progress + "%");*/
                                }
                            })
                    .continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            // gọi task để lấy URL sau khi upload thành công
                            return ref.getDownloadUrl();
                        }
                    })
                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri uri = task.getResult();
                                // upload thành công, lấy được url ảnh, ghi ra log. Bạn có thể ghi vào CSdl....
                                Log.d("TAG", "onComplete: url download = " + uri.toString());
                                AddBook(new Book(edtTenTruyen.getText().toString(),Integer.parseInt(edtGia.getText().toString()),uri.toString()));
                            } else {
                                // lỗi lấy url download
                            }
                        }
                    });
        }
    }

    void AddBook(Book book){
        db.collection("books")
                .add( book )
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(MainActivity.this, "Đăng truyện thành công", Toast.LENGTH_SHORT).show();
                        objBook.clear();
                        ReadBook();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("zzzz", "onFailure: lỗi thêm sách");
                        e.printStackTrace();
                    }
                });
    }

    void ReadBook(){
        db.collection("books")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Book book = document.toObject(Book.class);
                                objBook.add(book);
                            }
                            adapterBook.setData(objBook);
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this, RecyclerView.VERTICAL, false);
                            recyclerView.setLayoutManager(linearLayoutManager);
                            recyclerView.setAdapter(adapterBook);
                        } else {
                            Log.w("TAG", "Error getting documents.", task.getException());
                        }
                    }
                });
    }
}