package com.example.qrcodescanner.ui.slideshow;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.qrcodescanner.R;
import com.example.qrcodescanner.databinding.FragmentSlideshowBinding;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SlideshowFragment extends Fragment {

    EditText etInput;
    Button btGenerate;
    ImageView ivOutput;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 2;

    View root;

    private FragmentSlideshowBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        SlideshowViewModel slideshowViewModel = new ViewModelProvider(this).get(SlideshowViewModel.class);

        binding = FragmentSlideshowBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        root = binding.getRoot();

        etInput = root.findViewById(R.id.et_input);
        btGenerate = root.findViewById(R.id.bt_generate);
        ivOutput = root.findViewById(R.id.iv_output);

        btGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sText = etInput.getText().toString().trim();
                if (sText.equals("")) return;

                MultiFormatWriter writer = new MultiFormatWriter();

                try {
                    SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                    int defaultValue = 350;
                    int highScore = sharedPref.getInt("codeSize", defaultValue);

                    BitMatrix matrix = writer.encode(sText, BarcodeFormat.CODE_128, highScore,highScore);
                    BarcodeEncoder encoder = new BarcodeEncoder();
                    Bitmap bitmap = encoder.createBitmap(matrix);
                    ivOutput.setImageBitmap(bitmap);

                    InputMethodManager manager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    manager.hideSoftInputFromWindow(etInput.getApplicationWindowToken(),0);
                } catch (WriterException e) {
                    e.printStackTrace();
                }
            }
        });

        ivOutput.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Bitmap bm =((BitmapDrawable)ivOutput.getDrawable()).getBitmap();
                SaveImage(bm);

                return false;
            }
        });
        return root;
    }
    private void SaveImage(Bitmap finalBitmap) {

        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
        File myDir = new File(root + "/saved_images");
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        String defaultValue = myDir.toString();
        String highScore = sharedPref.getString("photoFolder", defaultValue);
        myDir = new File(highScore);
        myDir.mkdirs();

        String ts = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        finalBitmap.compress(Bitmap.CompressFormat.JPEG, 40, bytes);

        File file = new File(myDir + File.separator + ts+".jpg");

        FileOutputStream fileOutputStream = null;
        try {
            file.createNewFile();
            fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(bytes.toByteArray());

            Toast.makeText(getContext(),
                    file.getAbsolutePath(),
                    Toast.LENGTH_LONG).show();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(getContext(),"error", Toast.LENGTH_SHORT).show();
        } finally {
            if(fileOutputStream != null){
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

    }

    private  boolean checkAndRequestPermissions() {
        int permissionReadStorage = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        int permissionWriteStorage = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);

        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionWriteStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (permissionReadStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(getActivity(), listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}