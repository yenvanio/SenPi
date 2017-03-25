package com.shiv.outsourced.senpi;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.List;

/**
 * Created by 100603498 on 3/8/2017.
 */

public class QRFragment extends Fragment {

    Button generate;
    EditText temp;
    EditText name;
    Switch temp_switch;
    qrCodeList mCallback;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_qr, container, false);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (qrCodeList) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement OnHeadlineSelectedListener");
        }

        final Spinner spinner = (Spinner) view.findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.light_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        temp = (EditText) view.findViewById(R.id.temp);
        temp_switch = (Switch)view.findViewById(R.id.temp_switch);
        name = (EditText) view.findViewById(R.id.name);

        generate = (Button) view.findViewById(R.id.addButton);
        generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String light = spinner.getSelectedItem().toString();
                String temperature = temp.getText().toString();
                if (temp_switch.isChecked()) {
                    temperature += " °F";
                } else {
                    temperature += " °C";
                }
                String title = name.getText().toString();

                String contents = title + ", " + light + ", " + temperature;

                MultiFormatWriter mFW = new MultiFormatWriter();
                try {
                    BitMatrix bMatrix = mFW.encode(contents, BarcodeFormat.QR_CODE, 200, 200);
                    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                    Bitmap bitmap = barcodeEncoder.createBitmap(bMatrix);

                    final QRCode code = new QRCode();
                    code.setBitmap(bitmap);
                    code.setName(title);
                    code.setLighting(light);
                    code.setTemp(temperature);

                    mCallback = (QRFragment.qrCodeList) getActivity();
                    mCallback.addQRCode(code);

                    Toast.makeText(getActivity(), "QR Code Generated", Toast.LENGTH_SHORT).show();
                } catch (WriterException e) {
                    e.printStackTrace();
                } catch (ClassCastException e) {
                    throw new ClassCastException(getActivity().toString()
                            + " must implement OnHeadlineSelectedListener");
                }

            }
        });

        return view;
    }

    public interface qrCodeList {
        public List<QRCode> getCodeList();

        public void addQRCode(QRCode qrCode);
    }
}
