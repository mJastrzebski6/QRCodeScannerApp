package com.example.qrcodescanner.ui.settings;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.DocumentsContract;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.example.qrcodescanner.BuildConfig;
import com.example.qrcodescanner.MainActivity;
import com.example.qrcodescanner.R;
import com.example.qrcodescanner.databinding.FragmentSettingsBinding;
import com.example.qrcodescanner.ui.Utils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SettingsFragment extends Fragment {

    View root;
    private SettingsViewModel mViewModel;
    private FragmentSettingsBinding binding;
    private static final int PICKFILE_REQUEST_CODE = 100;

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        EditText destination_et = root.findViewById(R.id.destination_et);
        Button saveFolderButton = root.findViewById(R.id.change_destination_button);
        Switch color_switch = root.findViewById(R.id.color_switch);

        Button save_size_bt = root.findViewById(R.id.save_size_bt);
        EditText size_et = root.findViewById(R.id.size_et);

        save_size_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str = size_et.getText().toString();
                try{
                    int number = Integer.parseInt(str);
                    SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putInt("codeSize", number);
                    editor.apply();
                    Toast.makeText(getActivity(), "Zapisano", Toast.LENGTH_SHORT).show();
                }
                catch (NumberFormatException ex){
                    ex.printStackTrace();
                    Toast.makeText(getActivity(), "Zły rozmiar", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Fragment fg = this;

    saveFolderButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String str = destination_et.getText().toString();
            String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();

            Path myDir = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                myDir = Paths.get(root + "/" + str);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if(Files.isDirectory(myDir)){
                     SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                     SharedPreferences.Editor editor = sharedPref.edit();
                     editor.putString("photoFolder", myDir.toString());
                     editor.apply();
                     Toast.makeText(getActivity(), "Zapisano", Toast.LENGTH_SHORT).show();
                 }
                else{
                    Toast.makeText(getActivity(), "Zła ścieżka", Toast.LENGTH_SHORT).show();
                }
            }
        }
    });

       color_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
           @Override
           public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
               if(color_switch.isChecked()){
                    ((MainActivity)getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFBB86FC")));
                }
                else{
                    ((MainActivity)getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FF03DAC5")));
                }
           }
       });
        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICKFILE_REQUEST_CODE) {
            String FilePath = data.getData().getPath();
            String FileName = data.getData().getLastPathSegment();
            int lastPos = FilePath.length() - FileName.length();
            String Folder = FilePath.substring(0, lastPos);


            Toast.makeText(getActivity(), Folder, Toast.LENGTH_LONG
            ).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(SettingsViewModel.class);
        // TODO: Use the ViewModel
    }

}