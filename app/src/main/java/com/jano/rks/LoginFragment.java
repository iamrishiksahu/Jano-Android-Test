package com.jano.rks;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.truecaller.android.sdk.ITrueCallback;
import com.truecaller.android.sdk.TrueError;
import com.truecaller.android.sdk.TrueProfile;
import com.truecaller.android.sdk.TruecallerSDK;
import com.truecaller.android.sdk.TruecallerSdkScope;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginFragment extends Fragment {


    private Button authBtn;

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initializing treucaller SDK
        initializeTrueSdk(getContext());


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        authBtn = view.findViewById(R.id.authButton);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        authBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    JSONObject props = new JSONObject();
                    props.put("at", System.currentTimeMillis());
                    AppWide.mixpanel.track("Truecaller login clicked", props);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                //check if device contains truecaller app

                if(TruecallerSDK.getInstance().isUsable()){
                    //device contains truecaller app
                    TruecallerSDK.getInstance().getUserProfile(LoginFragment.this);

                }else{
                    //truecaller app not found
                    //skipping this for now
                    Toast.makeText(getContext(), "Truecaller app not found.", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("CONSOLEDATA", "onActivityResult: " + requestCode + " " + resultCode );

        if (requestCode == TruecallerSDK.SHARE_PROFILE_REQUEST_CODE) {
            TruecallerSDK.getInstance().onActivityResultObtained(getActivity(), requestCode, resultCode, data);
        }
    }

    private void trackCallBack (String status){
        try {
            JSONObject props = new JSONObject();
            props.put("at", System.currentTimeMillis());
            props.put("status", status);
            AppWide.mixpanel.track("Truecaller callback", props);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private final ITrueCallback sdkCallback = new ITrueCallback() {

        @Override
        public void onSuccessProfileShared(@NonNull final TrueProfile trueProfile) {
            trackCallBack("success");
            Log.d("CONSOLEDATA", "onSuccessProfileShared: " + "success");
            Toast.makeText(getContext(), "Successfully authenticated!", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(getContext(), MainActivity.class);
            startActivity(intent);
        }

        @Override
        public void onFailureProfileShared(@NonNull final TrueError trueError) {
            trackCallBack("failure");
            Log.d("CONSOLEDATA", "onFailureProfileShared: " + trueError.toString());

            Toast.makeText(getContext(), "Failed to share profile!", Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onVerificationRequired(@Nullable final TrueError trueError) {
            trackCallBack("verification required");
            Log.d("CONSOLEDATA", "onVerificationRequired: " + "ver");
            Toast.makeText(getContext(), "Verification required!", Toast.LENGTH_SHORT).show();

        }

    };

    private void initializeTrueSdk(Context ctx){

        TruecallerSdkScope trueScope = new TruecallerSdkScope.Builder(ctx, sdkCallback)
                .consentMode(TruecallerSdkScope.CONSENT_MODE_BOTTOMSHEET)
//                .buttonColor(Color.parseColor(colorSpinner.getSelectedItem().toString()))
//                .buttonTextColor(Color.parseColor(colorTextSpinner.getSelectedItem().toString()))
                .loginTextPrefix(TruecallerSdkScope.LOGIN_TEXT_PREFIX_TO_GET_STARTED)
                .loginTextSuffix(TruecallerSdkScope.LOGIN_TEXT_SUFFIX_PLEASE_VERIFY_MOBILE_NO)
                .ctaTextPrefix(TruecallerSdkScope.CTA_TEXT_PREFIX_USE)
                .buttonShapeOptions(TruecallerSdkScope.BUTTON_SHAPE_ROUNDED)
                .privacyPolicyUrl("<<YOUR_PRIVACY_POLICY_LINK>>")
                .termsOfServiceUrl("<<YOUR_PRIVACY_POLICY_LINK>>")
                .footerType(TruecallerSdkScope.FOOTER_TYPE_NONE)
                .consentTitleOption(TruecallerSdkScope.SDK_CONSENT_TITLE_LOG_IN)
                .sdkOptions(TruecallerSdkScope.SDK_OPTION_WITHOUT_OTP)
                .build();

        TruecallerSDK.init(trueScope);
    }

}