package id.fake.gps.main;

import android.content.Intent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import xxx.FA;
import xxx.FGS;
import xxx.HA;
import xxx.SA;

public class EventMain {
    public static final short REQUEST_CODE_FROM_SEARCH = 100;
    public static final short REQUEST_CODE_FROM_HISTORY = 200;
    public static final short REQUEST_CODE_FROM_FAVORITE = 300;

    public void favorite(View view) {
        ((AppCompatActivity) view.getContext()).startActivityForResult(new Intent(view.getContext(), FA.class), REQUEST_CODE_FROM_FAVORITE);
    }

    public void history(View view) {
        ((AppCompatActivity) view.getContext()).startActivityForResult(new Intent(view.getContext(), HA.class), REQUEST_CODE_FROM_HISTORY);
    }

    public void search(View view) {
        ((AppCompatActivity) view.getContext()).startActivityForResult(
                new Intent(view.getContext(), SA.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION), REQUEST_CODE_FROM_SEARCH
        );
    }

    public void stopFakeGps(View view) {
        view.getContext().stopService(new Intent(view.getContext(), FGS.class));
    }
}