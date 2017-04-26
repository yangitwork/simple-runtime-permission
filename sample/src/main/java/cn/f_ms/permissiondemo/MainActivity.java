package cn.f_ms.permissiondemo;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import cn.f_ms.runtimepermission.simple.Permission;
import cn.f_ms.runtimepermission.simple.ShowRequestPermissionRationaleListener;
import cn.f_ms.runtimepermission.simple.SimpleRuntimePermission;
import cn.f_ms.runtimepermission.simple.SimpleRuntimePermissionHelper;
import cn.f_ms.runtimepermission.simple.rxjava2.None;
import cn.f_ms.runtimepermission.simple.rxjava2.PermissionException;
import cn.f_ms.runtimepermission.simple.rxjava2.RxSimpleRuntimePermission;
import cn.f_ms.runtimepermission.simple.rxjava2.RxSimpleRuntimePermissionTransform;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.operators.maybe.MaybeCache;


public class MainActivity extends AppCompatActivity {

    private Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mActivity = this;

        final String requestSuccessStr = "Congratulations, You Geted Permission";

        findViewById(R.id.btn_request_with_base).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestPermissionWithBase(requestSuccessStr);
            }
        });
        findViewById(R.id.btn_request_with_rxjava1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestPermissionWithRxJava1(requestSuccessStr);
            }
        });
        findViewById(R.id.btn_request_with_rxjava2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestPermissionWithRxJava2(requestSuccessStr);
            }
        });


    }

    private void requestPermissionWithRxJava2(final String requestSuccessStr) {
        RxSimpleRuntimePermission rxSimpleRuntimePermission = new RxSimpleRuntimePermission(mActivity);

        rxSimpleRuntimePermission.request(new MyShowRequestPermissionRationaleListener(mActivity), Manifest.permission.READ_CONTACTS, Manifest.permission.CALL_PHONE)
                .subscribe(new Observer<None>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull None none) {
                        Toast.makeText(mActivity, requestSuccessStr, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        if (e instanceof PermissionException) {
                            Toast.makeText(mActivity, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });

//        Observable.just(requestSuccessStr)
//                .compose(rxSimpleRuntimePermission.<String>compose(new MyShowRequestPermissionRationaleListener(mActivity), Manifest.permission.READ_CONTACTS, Manifest.permission.CALL_PHONE))
//                .subscribe(new Observer<String>() {
//                    @Override
//                    public void onSubscribe(@NonNull Disposable d) {
//
//                    }
//
//                    @Override
//                    public void onNext(@NonNull String s) {
//                        Toast.makeText(mActivity, s, Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onError(@NonNull Throwable e) {
//
//                    }
//
//                    @Override
//                    public void onComplete() {
//
//                    }
//                });
    }

    private void requestPermissionWithRxJava1(String requestSuccessStr) {

    }

    private void requestPermissionWithBase(final String requestSuccessStr) {

        SimpleRuntimePermission simplePermission = new SimpleRuntimePermission(mActivity);

        SimpleRuntimePermissionHelper.with(simplePermission)
                .permission(Manifest.permission.READ_CONTACTS, Manifest.permission.CALL_PHONE)
                .showPermissionRationaleListener(new MyShowRequestPermissionRationaleListener(mActivity))
                .resultListener(new SimpleRuntimePermission.PermissionListener() {
                    @Override
                    public void onAllPermissionGranted() {
                        Toast.makeText(mActivity, requestSuccessStr, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRefuse(Permission[] allPermissionsResult, Permission[] grantedPermissionResult, Permission[] refusePermissionResult) {
                        String str = String.format("all: %s\ngranted: %s\nrefuse:%s",
                                allPermissionsResult, grantedPermissionResult, refusePermissionResult
                        );

                        Toast.makeText(mActivity, str, Toast.LENGTH_SHORT).show();
                    }
                })
                .execute();
    }

    private static class MyShowRequestPermissionRationaleListener implements ShowRequestPermissionRationaleListener {

        private Activity mActivity;

        public MyShowRequestPermissionRationaleListener(Activity activity) {

            mActivity = activity;
        }

        @Override
        public void onShowRequestPermissionRationale(final ShowRequestPermissionRationaleListener.ShowRequestPermissionRationaleControler controler, String[] permissions) {
            new AlertDialog.Builder(mActivity)
                    .setTitle("Tips")
                    .setMessage("Please Give Me Those Permissions")
                    .setPositiveButton("Yes, I Will", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            controler.doContinue();
                        }
                    })
                    .setNegativeButton("Sorry, I can't", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            controler.doCancel();
                        }
                    })
                    .setCancelable(false)
                    .create()
                    .show();
        }

        @Override
        public void onRequestPermissionRationaleRefuse(String[] permissions) {
            Toast.makeText(mActivity, "Well, You refused.", Toast.LENGTH_SHORT).show();
        }
    }
}
