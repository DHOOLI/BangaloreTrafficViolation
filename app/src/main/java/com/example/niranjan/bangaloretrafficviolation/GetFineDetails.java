package com.example.niranjan.bangaloretrafficviolation;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;



import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class GetFineDetails extends Fragment {
    private String TAG = MainActivity.class.getSimpleName();

    private ProgressDialog pDialog;
    private ListView lv;
TextView textView;
    // URL to get contacts JSON
    private  String url;

    ArrayList<HashMap<String, String>> fineList;

    public GetFineDetails() {
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
        View view = inflater.inflate(R.layout.fragment_get_fine_details, container, false);
        String reg_num = this.getArguments().getString("REG_NUM").toString();
        textView=(TextView)view.findViewById(R.id.No_fine_text);
        fineList = new ArrayList<>();
        url="https://www.karnatakaone.gov.in/PoliceCollectionOfFine/FineDetails?SearchBy=REGNO&SearchValue="+reg_num+"&ServiceCode=BPS";
        lv = (ListView)view.findViewById(R.id.list);

        new GetContacts().execute();
        return view;
    }
    /**
     * Async task class to get json by making HTTP call
     */
    private class GetContacts extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray FineDetails = jsonObj.getJSONArray("PoliceFineDetailsList");

                        // looping through All Contacts
                        for (int i = 0; i < FineDetails.length(); i++) {
                            JSONObject c = FineDetails.getJSONObject(i);

                            String NoticeNo = c.getString("NoticeNo");
                            String NoticeGenerationDate = c.getString("NoticeGenerationDate");
                            String ViolationDate = c.getString("ViolationDate");
                            String ViolationTime = c.getString("ViolationTime");
                            String PointName = c.getString("PointName");
                            String OffenceDescription = c.getString("OffenceDescription");
                            String FineAmount = c.getString("FineAmount");


                            // tmp hash map for single contact
                            HashMap<String, String> Fine = new HashMap<>();

                            // adding each child node to HashMap key => value
                            Fine.put("Offence", "Offence: " + OffenceDescription);
                            Fine.put("Place", "Place: " + PointName);
                            Fine.put("ViolationDate", "Violation Date: " + ViolationDate + "  Time: " + ViolationTime);
                            Fine.put("FineAmount", "Amount: " + FineAmount);
                            Fine.put("NoticeGenDate", "Notice Generation Date: " + NoticeGenerationDate);
                            Fine.put("NoticeNo", "Notice No: " + NoticeNo);


                            // adding contact to contact list
                            fineList.add(Fine);
                        }


                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                           // Toast.makeText(getContext(),
                             //       "Json parsing error: " + e.getMessage(),
                               //     Toast.LENGTH_LONG)
                                 //   .show();
                            textView.setVisibility(View.VISIBLE);
                            textView.setText("NO VIOLATIONS FOUND ON THIS VEHICLE\n Drive Cool...");
                        }
                    });

                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */
            ListAdapter adapter = new SimpleAdapter(
                    getActivity(), fineList,
                    R.layout.list_item, new String[]{"Offence","Place","ViolationDate","FineAmount","NoticeGenDate","NoticeNo"},
                    new int[]{R.id.Offence, R.id.Place,R.id.ViolatonDate,R.id.FineAmount,R.id.NoticeGenDate,R.id.NoticeNo});

            lv.setAdapter(adapter);
        }
    }




}
