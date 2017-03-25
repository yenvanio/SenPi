package com.shiv.outsourced.senpi;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 100603498 on 3/8/2017.
 */

public class HomeFragment extends Fragment {

    private User user;
    private List<QRCode> codes;
    qrCodeList mCallback;
    RecyclerView rv;
    RVAdapter adapter;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (qrCodeList) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement OnHeadlineSelectedListener");
        }

        user = (User) getArguments().getSerializable("user");
        codes = mCallback.getCodeList();

        TextView welcome = (TextView) view.findViewById(R.id.welcome);
        TextView errno = (TextView) view.findViewById(R.id.errno);

        welcome.setText("Welcome, " + user.getName());

        if (codes.isEmpty()) {
            errno.setText(R.string.errno);
        }

        else
            {
                errno.setVisibility(View.GONE);
                welcome.setVisibility(View.GONE);
                rv = (RecyclerView) view.findViewById(R.id.rv);
                rv.setLayoutManager(new LinearLayoutManager(getActivity()));
                adapter = new RVAdapter(codes, getActivity());
                rv.setAdapter(adapter);
            }

        return view;
    }

    public interface qrCodeList
    {
        public List<QRCode> getCodeList();
        public void addQRCode(QRCode qrCode);
    }


}
