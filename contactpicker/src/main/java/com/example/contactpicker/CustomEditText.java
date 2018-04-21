package com.example.contactpicker;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;

/**
 * Created by root on 21/4/18.
 */
public class CustomEditText extends android.support.v7.widget.AppCompatEditText
{
    public CustomEditText( Context context )
    {
        super( context );
    }
    public CustomEditText(Context context, AttributeSet attribute_set )
    {
        super( context, attribute_set );
    }
    public CustomEditText( Context context, AttributeSet attribute_set, int def_style_attribute )
    {
        super( context, attribute_set, def_style_attribute );
    }
    @Override
    public boolean onKeyPreIme( int key_code, KeyEvent event )
    {
        if ( event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP ){}
        //this.clearFocus();
        return super.onKeyPreIme( key_code, event );
    }
}