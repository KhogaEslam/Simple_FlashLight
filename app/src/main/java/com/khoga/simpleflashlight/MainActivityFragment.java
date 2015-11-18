package com.khoga.simpleflashlight;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.app.AlertDialog;
//import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private Camera camera;
    private boolean isFlashOn;
    private boolean hasFlash;
    Camera.Parameters param;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        hasFlash = this.getContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

        if(!hasFlash){
            AlertDialog alert = new AlertDialog.Builder(this.getContext()).create();
            alert.setTitle("Error Message");
            alert.setMessage("Your Device doesn't support flash light! I'm Going to Sleep Now -_-");
            alert.setButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    getActivity().finish();
                }
            });
            alert.show();
            return rootView;
        }

        getCamera();

        ToggleButton flashSwitch = (ToggleButton) rootView.findViewById(R.id.flash_switch);

        flashSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    turnOffFlash();
                }
                else{
                    turnOnFlash();
                }
            }
        });
        return rootView;
    }

    private void turnOnFlash() {
        if(!isFlashOn){
            if(camera == null || param == null){
                return;
            }

            param = camera.getParameters();
            param.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            camera.setParameters(param);
            camera.startPreview();

            isFlashOn = true;

            Log.v("Flash is On", "Flash has been turned On...");
        }
    }

    private void turnOffFlash() {
        if(isFlashOn){
            if(camera == null || param == null){
                return;
            }

            //param = camera.getParameters();
            param.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            camera.setParameters(param);
            camera.stopPreview();

            isFlashOn = false;

            Log.v("Flash is Off", "Flash has been turned Off...");
        }
    }

    private void getCamera() {
        if(camera == null){
            try{
                camera = Camera.open();
                param = camera.getParameters();
            }
            catch (RuntimeException e){
                Log.e("Camera Error","Failed To Open Camera. Error: "+e.getMessage());
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        turnOffFlash();
    }

    @Override
    public void onStart() {
        super.onStart();
        getCamera();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(camera != null){
            camera.release();
            camera = null;
        }
    }
}
