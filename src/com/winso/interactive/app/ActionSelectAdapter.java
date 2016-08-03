package com.winso.interactive.app;
import com.winso.interactive.R;
import com.winso.interactive.app.LazyAdapter.MyDat;
import com.winso.comm_library.icedb.SelectHelp;

import java.util.ArrayList;    
import java.util.HashMap;    
import java.util.List;    
import java.util.Map;    
    
import android.content.Context;    
import android.view.LayoutInflater;    
import android.view.View;    
import android.view.ViewGroup;    
import android.widget.BaseAdapter;    
import android.widget.CheckBox;    
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;    
import android.view.View.OnClickListener;

public class ActionSelectAdapter extends BaseAdapter {    
    private LayoutInflater mInflater;    
    private List<Map<String, Object>> mData;    
    public static Map<Integer, Boolean> isSelected;    
    
    ListView mListView;
    ViewHolderAction mPreviousHolder = null;
    public boolean mBSingleSelected = false;
    public ActionSelectAdapter(Context context,ListView v) {    
        mInflater = LayoutInflater.from(context);    
        init(v);    
    }    
    
    //初始化    
    private void init(ListView v) {    
    	
    	mListView = v;
        mData=new ArrayList<Map<String, Object>>();    
//        for (int i = 0; i < 5; i++) {    
//            Map<String, Object> map = new HashMap<String, Object>();    
//            //map.put("img", R.drawable.icon);    
//            map.put("list_item_title", "第" + (i + 1) + "行的标题");  
//            map.put("detail_id", "" + (i + 1) );    
//            mData.add(map);    
//        }    
//        //这儿定义isSelected这个map是记录每个listitem的状态，初始状态全部为false。    
//        isSelected = new HashMap<Integer, Boolean>();    
//        for (int i = 0; i < mData.size(); i++) {    
//            isSelected.put(i, false);    
//        }    
    }   
    public void processUncheck(long id)
    {
    	if ( id >= mData.size() ) return;
    	
    	for (int j = 0; j < mData.size(); j++) {
			// DrawArea_Steptwo.gridview.getChildAt(j);

			// Toast.makeText(context, "Rajesh",
			// Toast.LENGTH_SHORT).show();
			RelativeLayout gridchildlayout = (RelativeLayout) mListView
					.getChildAt(j);

			if ( gridchildlayout == null ) continue;
			
			CheckBox tempRadioToggle = (CheckBox) gridchildlayout
					.findViewById(R.id.ck_action);

			
			if ( j== id )
				tempRadioToggle.setChecked(true);
			else
				tempRadioToggle.setChecked(false);
		}
            
    	 for (int i = 0; i < mData.size(); i++) {    
             isSelected.put(i, false);    
         } 
    	 
    	 isSelected.put((int) id, true); 
    	 ActionSelectAdapter.this.notifyDataSetChanged();
    }
    public void updateData(SelectHelp help)
    {
    	mData.clear();
        //mData=new ArrayList<Map<String, Object>>();    
        for (int i = 0; i < help.size(); i++) {    
            Map<String, Object> map = new HashMap<String, Object>();    
            //map.put("img", R.drawable.icon);    
            map.put("title_id", help.valueStringByName(i, "title_id"));  
            map.put("title_left", help.valueStringByName(i, "title_left") );    
            mData.add(map);    
        }    
        //这儿定义isSelected这个map是记录每个listitem的状态，初始状态全部为false。    
        isSelected = new HashMap<Integer, Boolean>();    
        for (int i = 0; i < mData.size(); i++) {    
            isSelected.put(i, false);    
        } 
    }
    
    @Override    
    public int getCount() {    
        return mData.size();    
    }    
    
    @Override    
    public Object getItem(int position) {   
    	if ( position >= mData.size() ) return null;
    	
    	return mData.get(position);
    	    
    }    
    
    @Override    
    public long getItemId(int position) {

    	return 0;
    
    }    
    public String getNameStrings()
    {
    	String sNames="";
        for (int i = 0; i < mData.size(); i++) {    
        	if( isSelected.get(i) )
        	{
        		Map<String, Object> map =mData.get(i);
        		sNames += map.get("title_left").toString();
        		sNames += "\n";
        	}
        } 	
        
        return sNames;
    }
    
    @Override    
    public View getView(int position1, View convertView, ViewGroup parent) {    
    	ViewHolderAction holder = null;    
    	final int position = position1;  
    	
    	
        //convertView为null的时候初始化convertView。    
        if (convertView == null) {    
            holder = new ViewHolderAction();    
            convertView = mInflater.inflate(R.layout.list_view_select_action, null);    
            holder.title_id = (TextView) convertView.findViewById(R.id.title_id);    
            holder.title_left = (TextView) convertView.findViewById(R.id.title_left);   
            
            holder.title_left.setTextSize((float) 13);
            holder.cBox = (CheckBox) convertView.findViewById(R.id.ck_action);    
            convertView.setTag(holder);    
        } else {    
            holder = (ViewHolderAction) convertView.getTag();    
        }    
        
       
      
        holder.title_id.setText(mData.get(position).get("title_id").toString());   
        holder.title_left.setText(mData.get(position).get("title_left").toString());    
        holder.cBox.setChecked(isSelected.get(position));    
        
      
        
        return convertView;    
    }    
    
    public final class ViewHolderAction {    
        public TextView title_id;    
        public TextView title_left;    
        public CheckBox cBox;    
    }    
}    