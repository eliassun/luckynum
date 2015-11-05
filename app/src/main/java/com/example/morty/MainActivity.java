package com.example.morty;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    final     String TAG = "MainActivity";
    Button    btn_findLuckNumber;
    EditText  editview_luckynum;
    TextView  textView_result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        editview_luckynum      =  (EditText)findViewById(R.id.editview_lucklist);
        btn_findLuckNumber     =  (Button)findViewById(R.id.btn_findLuckNumber);
        textView_result        =  (TextView)findViewById(R.id.textview_result);

        btn_findLuckNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] splitResults = splitLongStr(editview_luckynum.getText().toString());
                if(splitResults==null)
                    return;
                List<String> l= Arrays.asList(splitResults);
                ArrayList<String> luckyStrList = new ArrayList<String>(l);
                ArrayList<String> resultList = GetLottery(luckyStrList);
                String results="";
                for(String tmp:resultList)
                  results += tmp+System.getProperty("line.separator");
                textView_result.setText(results);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class SearchDigitsResults
    {
        boolean  found;
        String   foundstr;
    }

    private ArrayList<String> GetLottery(ArrayList<String> luckylist) {
        if(luckylist==null)
            return null;
        ArrayList<String> results  = new ArrayList<String>();
        String foundLuckyStr="";
        HashMap<String,Integer> hm = new HashMap<String,Integer>();

        for(String tmp:luckylist) {
            if((tmp.length()<7)||(tmp.length()>14))
                continue;
            hm.clear();
            for(int i=1;i<=59;i++) {
                hm.put(String.valueOf(i),0);
            }
            foundLuckyStr = SearchLuckyDigits(tmp,0,hm).foundstr;
            if (foundLuckyStr.length()>7)
            {
                Log.d(TAG,foundLuckyStr);
                results.add(foundLuckyStr);
            }
        }
        return results;
    }

    private boolean IsCorrectSingleDigit(String toCheckString, int depth, HashMap<String,Integer> foundDigitsMap) {
        if((depth>7)||(toCheckString==null)||(toCheckString.length()==0)
                ||(!foundDigitsMap.containsKey(toCheckString.substring(0,1)))||(foundDigitsMap.get(toCheckString.substring(0,1))>0))
            return false;

        return true;
    }

    private boolean IsCorrectDoubleDigits(String toCheckString, int depth, HashMap<String,Integer> foundDigitsMap) {
        if((depth>7)||(toCheckString==null)||(toCheckString.length()<2)
                ||(!foundDigitsMap.containsKey(toCheckString.substring(0,2)))||(foundDigitsMap.get(toCheckString.substring(0,2))>0))
            return false;
        return true;
    }

    private SearchDigitsResults SearchLuckyDigits(String digits, int depth, HashMap<String,Integer> foundDigitsMap)
    {
        SearchDigitsResults r = new SearchDigitsResults();
        r.found    = false;
        r.foundstr = "";

        if((depth <7) && ((digits==null)||(digits.length()==0))){
            return r;
        }

        if (((depth == 7)&& (digits!=null) &&(digits.length()==0))
                || ((depth==7)&&(digits==null))){
            r.found = true;
            r.foundstr = "";
            return r;
        }
        boolean dd = IsCorrectDoubleDigits(digits, depth, foundDigitsMap);
        boolean sd = IsCorrectSingleDigit(digits, depth, foundDigitsMap);

        if(dd) {
            String  key   = digits.substring(0,2);
            Integer value = foundDigitsMap.get(key);
            foundDigitsMap.put(key,++value);

            SearchDigitsResults tmpr= SearchLuckyDigits(digits.substring(2),depth+1,foundDigitsMap);
            if(tmpr.found)
            {
                r.foundstr = digits.substring(0,2) + " " + tmpr.foundstr;
                r.found    = true;
                return r;
            }else {
                foundDigitsMap.put(key,((value-1)>=0)?(value-1):0);
                if (sd) {
                    key    = digits.substring(0,1);
                    value  = foundDigitsMap.get(key);
                    foundDigitsMap.put(key,++value);

                    tmpr = SearchLuckyDigits(digits.substring(1),depth+1,foundDigitsMap);
                    if (tmpr.found) {
                        r.foundstr = digits.substring(0,1)  + " " +  tmpr.foundstr;
                        r.found = true;
                        return r;
                    }else {
                        foundDigitsMap.put(key,((value-1)>=0)?(value-1):0);
                    }
                }
            }
        }else if(sd) {
            String  key   = digits.substring(0,1);
            Integer value = foundDigitsMap.get(key);
            foundDigitsMap.put(key,++value);

            SearchDigitsResults tmpr= SearchLuckyDigits(digits.substring(1),depth+1,foundDigitsMap);
            if(tmpr.found)
            {
                r.foundstr = digits.substring(0,1)  + " " +  tmpr.foundstr;
                r.found    = true;
                return r;
            }else {
                foundDigitsMap.put(key,((value-1)>=0)?(value-1):0);
                if (dd) {
                    key    = digits.substring(0,2);
                    value  = foundDigitsMap.get(key);
                    foundDigitsMap.put(key,++value);

                    tmpr = SearchLuckyDigits(digits.substring(2),depth+1,foundDigitsMap);
                    if (tmpr.found) {
                        r.foundstr = digits.substring(0,2)  + " " +  tmpr.foundstr;
                        r.found = true;
                        return r;
                    }else {
                        foundDigitsMap.put(key,((value-1)>=0)?(value-1):0);
                    }
                }
            }
        }
        return r;
    }

    private String[] splitLongStr(String toSplit)
    {
        if(toSplit==null)
            return null;
        String removeSpace=toSplit.replace(" ","");
        toSplit=removeSpace;
        String[] separated = toSplit.split(",");
        if(separated==null)
            return null;
        int len = separated.length;
        for(int i=0;i<len;i++) {
            String tmp=separated[i].replace("\"","");
            separated[i] = tmp;
            tmp = separated[i].replace("“","");
            separated[i] = tmp;
            tmp = separated[i].replace("”","");
            separated[i] = tmp;
        }
        return separated;
    }
}
