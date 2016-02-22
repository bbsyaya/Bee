package com.lingshi321.bee.ui;

import java.util.HashMap;
import java.util.Map;

import org.apache.harmony.javax.security.sasl.RealmCallback;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.zxing.client.android.CaptureActivity;
import com.lingshi321.bee.LoginActivity;
import com.lingshi321.bee.MainActivity;
import com.lingshi321.bee.R;
import com.lingshi321.bee.WebViewActivity;
import com.lingshi321.bee.common.BoundOrderRunnable;
import com.lingshi321.bee.common.CheckVersionRunnable;
import com.lingshi321.bee.common.GetOrganizationRunnable;
import com.lingshi321.bee.common.LogoutRunnable;
import com.lingshi321.bee.domain.RequestResult;
import com.lingshi321.bee.util.DataInterfaceConstants;
import com.lingshi321.bee.util.SharedPreferencesUtil;
import com.lingshi321.bee.util.XmppTool;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("ValidFragment")
public class InformationFragment extends Fragment implements OnClickListener {

    private MainActivity mainActivity;

    private RelativeLayout check_version;
    private RelativeLayout scanner;
    private RelativeLayout webView;

    private TextView staff_name_value;
    private TextView staff_phone_value;
    private TextView staff_email_value;
    private TextView staff_join_time_value;
    private TextView check_version_value;
    private Button logout;
    private InformationHandler handler = null;
    private Dialog dialog = null;

    public static final int LOGOUT = 100;
    public static final int CHECKVERSION = 101;
    public static final int GETORGANIZATION = 102;
    public static final int BOUNDORDER = 103;

    // 扫码得到的订单ID
    private String orderId = "";

    private final String TAG = InformationFragment.class.getSimpleName();

    public InformationFragment(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        handler = new InformationHandler();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LinearLayout layout = (LinearLayout) inflater.inflate(
                R.layout.fragment_user_info, container, false);
        staff_name_value = (TextView) layout
                .findViewById(R.id.staff_name_value);
        staff_name_value.setText(SharedPreferencesUtil.getUserName());
        staff_phone_value = (TextView) layout
                .findViewById(R.id.staff_phone_value);
        staff_phone_value.setText(SharedPreferencesUtil.getUserPhone());
        staff_email_value = (TextView) layout
                .findViewById(R.id.staff_email_value);
        staff_email_value.setText(SharedPreferencesUtil.getUserEmail());
        staff_join_time_value = (TextView) layout
                .findViewById(R.id.staff_join_time_value);
        staff_join_time_value.setText(SharedPreferencesUtil.getUserJoinTime());
        check_version = (RelativeLayout) layout
                .findViewById(R.id.check_version);
        check_version_value = (TextView) layout
                .findViewById(R.id.check_version_value);
        check_version_value.setText(getCurrentVersionName());
        scanner = (RelativeLayout) layout.findViewById(R.id.scanner);
        webView = (RelativeLayout) layout.findViewById(R.id.more);
        logout = (Button) layout.findViewById(R.id.submit_logout);
        check_version.setOnClickListener(this);
        scanner.setOnClickListener(this);
        logout.setOnClickListener(this);
        webView.setOnClickListener(this);
        return layout;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submit_logout:
                dialog = new Dialog(getActivity(), R.style.dialogStyle);
                dialog.setContentView(R.layout.login_dialog);
                dialog.setCancelable(false);
                dialog.show();
                TextView content = (TextView) dialog
                        .findViewById(R.id.dialog_content);
                content.setText("注销中..");

                new Thread(new LogoutRunnable(handler)).start();
                break;
            case R.id.scanner:
                Intent intent = new Intent(mainActivity, CaptureActivity.class);
                startActivityForResult(intent, mainActivity.REQUESTCODE);
                break;
            case R.id.check_version:
                dialog = new Dialog(getActivity(), R.style.dialogStyle);
                dialog.setContentView(R.layout.login_dialog);
                dialog.setCancelable(false);
                dialog.show();
                TextView content1 = (TextView) dialog
                        .findViewById(R.id.dialog_content);
                content1.setText("请稍后...");
                new Thread(new CheckVersionRunnable(handler,
                        getCurrentVersionName())).start();
                break;
            case R.id.more:
                startActivity(new Intent(getActivity(), WebViewActivity.class));
                break;
            default:
                break;
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == CaptureActivity.BEE_RESULT && data != null) {
            dialog = new Dialog(getActivity(), R.style.dialogStyle);
            dialog.setContentView(R.layout.login_dialog);
            dialog.setCancelable(false);
            dialog.show();
            TextView content = (TextView) dialog
                    .findViewById(R.id.dialog_content);
            content.setText("加载中...");

            String result = data.getStringExtra(CaptureActivity.BEE_URL);
            orderId = result;

            Toast.makeText(mainActivity,
                    result,
                    Toast.LENGTH_LONG).show();

            // 从服务器获取寝室信息
            Map<String, String> params = new HashMap<String, String>();
            new Thread(new GetOrganizationRunnable(handler, params)).start();

        }
    }

    public class InformationHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            if (dialog != null && dialog.isShowing())
                dialog.dismiss();
            switch (msg.arg1) {
                case LOGOUT:
                    switch (msg.what) {
                        case RequestResult.NORMAL:
                            Logout();
                            break;
                        default:
                            Toast.makeText(getActivity().getApplication(),
                                    msg.obj.toString(), Toast.LENGTH_SHORT).show();
                            break;
                    }
                    break;
                case GETORGANIZATION:
                    switch (msg.what) {
                        case RequestResult.NORMAL:
                            try {
                                JSONArray orders = (JSONArray) msg.obj;
                                String[] infos = new String[orders.length()];
                                int[] ids = new int[orders.length()];
                                for (int index = 0; index < orders.length(); index++) {
                                    JSONObject data;

                                    data = orders.getJSONObject(index);

                                    infos[index] = data.getString("building_id") + "栋"
                                            + data.getString("dormitory_name");
                                    ids[index] = data.getInt("dormitory_id");
                                }
                                showDormitoryInfo(infos, ids);
                            } catch (Exception e) {
                                showDialogMessage("获取寝室信息失败!原因:加载数据异常 请重试!");
                                e.printStackTrace();
                            }
                            break;
                        default:
                            showDialogMessage("获取寝室信息失败!原因:" + msg.obj.toString() + " 请重试!");
                            break;
                    }
                    break;
                case BOUNDORDER:
                    switch (msg.what) {
                        case RequestResult.NORMAL:
                            showDialogMessage("绑定订单成功!");
                            break;
                        default:
                            showDialogMessage("绑定订单失败!原因:" + msg.obj.toString() + " 请重试!");
                            break;
                    }
                    break;
                case CHECKVERSION:
                    Toast.makeText(getActivity(), "已是最新版本!", Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }

        }

    }

    private void Logout() {

        SharedPreferencesUtil.setPhone("");
        SharedPreferencesUtil.setPassword("");
        // 退出openfire服务器
        // XmppTool.closeConnection();
        getActivity().startActivity(
                new Intent().setClass(getActivity(), LoginActivity.class));
        getActivity().finish();
    }

    /**
     * 展示寝室信息
     *
     * @param info
     * @param ids  对应的寝室id信息
     */
    private void showDormitoryInfo(final String[] info, final int[] ids) {
        if (info.length == 0) {
            showDialogMessage("您没有可配送的寝室!");
            return;
        }
        new AlertDialog.Builder(mainActivity).setTitle("请选择寝室")
                .setItems(info, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg, int which) {

                        Toast.makeText(mainActivity, info[which],
                                Toast.LENGTH_LONG).show();

                        Map<String, String> params = new HashMap<String, String>();
                        params.put(DataInterfaceConstants.DORMITORY_ID, ids[which] + "");
                        params.put(DataInterfaceConstants.STAFF_ID, SharedPreferencesUtil.getStaffId());
                        params.put(DataInterfaceConstants.ORDER_ID, orderId);
                        Log.i(TAG, "dormitory_id=" + ids[which] + "  staff_id=" + SharedPreferencesUtil.getStaffId() + "  order_id=" + orderId);
                        new Thread(new BoundOrderRunnable(handler, params)).start();

                        dialog = new Dialog(getActivity(), R.style.dialogStyle);
                        dialog.setContentView(R.layout.login_dialog);
                        dialog.setCancelable(false);
                        dialog.show();
                        TextView content = (TextView) dialog
                                .findViewById(R.id.dialog_content);
                        content.setText("加载中...");
                    }
                }).show();
    }


    private void showDialogMessage(String msg) {
        new AlertDialog.Builder(mainActivity)
                .setTitle("提示")
                .setMessage(msg)
                .setPositiveButton("确定", null)
                .show();
    }

    /**
     * 获取当前应用版本号
     *
     * @return
     */
    private String getCurrentVersionName() {
        PackageManager packageManager = mainActivity.getPackageManager();
        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(
                    mainActivity.getPackageName(), 0);
        } catch (NameNotFoundException e) {

            e.printStackTrace();
            return "0.0.0";
        }
        String version = packInfo.versionName;

        return version;
    }

}
