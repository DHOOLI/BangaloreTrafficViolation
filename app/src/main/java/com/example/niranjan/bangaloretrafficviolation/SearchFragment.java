package com.example.niranjan.bangaloretrafficviolation;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;




public class SearchFragment extends Fragment {

EditText registration_number;
 Button button_check;
 Button button_clear;
String reg_num;

    View root_view;

    public SearchFragment() {
        // Required empty public constructor
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root_view=inflater.inflate(R.layout.fragment_search,container,false);
        Context context=getActivity();
        final SharedPreferences preferences=context.getSharedPreferences("Last_reg_num",Context.MODE_PRIVATE);



        button_check=(Button) root_view.findViewById(R.id.check_button);
        button_clear=(Button) root_view.findViewById(R.id.clear_button);
        registration_number=(EditText) root_view.findViewById(R.id.reg_number);
        registration_number.setText(preferences.getString("num",null));
        button_check.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        if(isConnected()) {

            reg_num = registration_number.getText().toString();

            if (TextUtils.isEmpty(reg_num)) {
                registration_number.setError("Please Enter Registration Number");
                Toast.makeText(getContext(), "Please Enter Registration Number", Toast.LENGTH_SHORT).show();
            } else {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("num", reg_num);
                editor.commit();

                Bundle bundle = new Bundle();
                bundle.putString("REG_NUM", reg_num);
                Fragment fragment = new GetFineDetails();
                fragment.setArguments(bundle);
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame, fragment);
                fragmentTransaction.addToBackStack(null);

                fragmentTransaction.commit();
            }
        }
        else{
            Toast.makeText(getContext(),"Please Connect to the INTERNET",Toast.LENGTH_LONG).show();
        }




    }
});
    button_clear.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        registration_number.setText("");
    }
});
       return root_view;
       //return inflater.inflate(R.layout.fragment_search, container, false);
    }

    private boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }



}
