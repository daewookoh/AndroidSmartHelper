package com.and.smarthelper.util;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.and.smarthelper.R;
import com.and.smarthelper.application.MyApplication;

import java.util.List;

public class AppAdapter extends ArrayAdapter<AppList> {

    private final List<AppList> list;
    private final Activity context;

    MyApplication common = new MyApplication(getContext());

    public AppAdapter(Activity context, List<AppList> list) {
        super(context, R.layout.app_list, list);
        this.context = context;
        this.list = list;
    }

    static class ViewHolder {
        protected ImageView app_icon;
        protected TextView app_name;
        protected CheckBox app_checkbox;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = null;
        if (convertView == null) {
            LayoutInflater inflator = context.getLayoutInflater();
            view = inflator.inflate(R.layout.app_list, null);
            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.app_icon = (ImageView) view.findViewById(R.id.icon);
            viewHolder.app_name = (TextView) view.findViewById(R.id.label);
            viewHolder.app_checkbox = (CheckBox) view.findViewById(R.id.check);
            viewHolder.app_checkbox
                    .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                        @Override
                        public void onCheckedChanged(CompoundButton buttonView,
                                                     boolean isChecked) {
                            AppList element = (AppList) viewHolder.app_checkbox
                                    .getTag();
                            element.setSelected(buttonView.isChecked());

                            String package_name = "/"+element.getPackageName()+"/";
                            String package_name2 = "/"+element.getPackageName()+"_"+element.getAppName()+"/";
                            Log.d("TTT", package_name);

                            String allowed_app_list = common.getSP("allowed_app_list","EMPTY");
                            String allowed_app_list2 = common.getSP("allowed_app_list2","EMPTY");

                            if(isChecked)
                            {
                                if(allowed_app_list.contains(package_name))
                                {
                                    // Do Nothing
                                }
                                else {
                                    allowed_app_list += package_name;
                                    common.putSP("allowed_app_list", allowed_app_list);
                                    allowed_app_list2 += package_name2;
                                    common.putSP("allowed_app_list2", allowed_app_list2);
                                }
                            }
                            else
                            {
                                String new_list = allowed_app_list.replaceAll(package_name,"");
                                common.putSP("allowed_app_list", new_list);
                                String new_list2 = allowed_app_list2.replaceAll(package_name2,"");
                                common.putSP("allowed_app_list2", new_list2);
                                }

                            allowed_app_list = common.getSP("allowed_app_list","");
                            //Log.d("TTT", allowed_app_list);
                            //Log.d("TTT", allowed_app_list2);

                        }
                    });
            view.setTag(viewHolder);
            viewHolder.app_checkbox.setTag(list.get(position));
        } else {
            view = convertView;
            ((ViewHolder) view.getTag()).app_checkbox.setTag(list.get(position));
        }
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.app_icon.setImageDrawable(list.get(position).getIcon());
        holder.app_name.setText(list.get(position).getAppName());
        holder.app_checkbox.setChecked(list.get(position).isSelected());
        return view;
    }
}
