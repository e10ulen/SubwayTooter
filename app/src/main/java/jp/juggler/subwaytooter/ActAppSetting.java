package jp.juggler.subwaytooter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;

import com.jrummyapps.android.colorpicker.ColorPickerDialog;
import com.jrummyapps.android.colorpicker.ColorPickerDialogListener;

public class ActAppSetting extends AppCompatActivity
	implements CompoundButton.OnCheckedChangeListener
	, AdapterView.OnItemSelectedListener
	, View.OnClickListener
	, ColorPickerDialogListener, TextWatcher
{
	
	public static void open( ActMain activity, int request_code ){
		activity.startActivityForResult( new Intent( activity, ActAppSetting.class ), request_code );
	}
	
	SharedPreferences pref;
	
	@Override
	protected void onCreate( @Nullable Bundle savedInstanceState ){
		super.onCreate( savedInstanceState );
		App1.setActivityTheme( this, false );
		initUI();
		pref = Pref.pref( this );
		
		loadUIFromData();
		
	}
	
	Switch swDontConfirmBeforeCloseColumn;
	Switch swPriorLocalURL;
	Switch swDisableFastScroller;
	Switch swSimpleList;
	Switch swExitAppWhenCloseProtectedColumn;
	Switch swShowFollowButtonInButtonBar;
	Switch swDontRound;
	Switch swDontUseStreaming;
	Switch swDontRefreshOnResume;
	Switch swDontScreenOff;
	Switch swDisableTabletMode;
	
	Spinner spBackButtonAction;
	Spinner spUITheme;
	Spinner spResizeImage;
	Spinner spRefreshAfterToot;
	
	CheckBox cbNotificationSound;
	CheckBox cbNotificationVibration;
	CheckBox cbNotificationLED;
	
	static final int BACK_ASK_ALWAYS = 0;
	static final int BACK_CLOSE_COLUMN = 1;
	static final int BACK_OPEN_COLUMN_LIST = 2;
	static final int BACK_EXIT_APP = 3;
	
	int footer_button_bg_color;
	int footer_button_fg_color;
	int footer_tab_bg_color;
	int footer_tab_divider_color;
	
	ImageView ivFooterToot;
	ImageView ivFooterMenu;
	View llFooterBG;
	View vFooterDivider1;
	View vFooterDivider2;
	
	EditText etColumnWidth;
	EditText etMediaThumbHeight;
	
	private void initUI(){
		setContentView( R.layout.act_app_setting );
		
		
		Styler.fixHorizontalPadding(findViewById( R.id.svContent ));
		
		
		swDontConfirmBeforeCloseColumn = (Switch) findViewById( R.id.swDontConfirmBeforeCloseColumn );
		swDontConfirmBeforeCloseColumn.setOnCheckedChangeListener( this );
		
		swPriorLocalURL = (Switch) findViewById( R.id.swPriorLocalURL );
		swPriorLocalURL.setOnCheckedChangeListener( this );
		
		swDisableFastScroller = (Switch) findViewById( R.id.swDisableFastScroller );
		swDisableFastScroller.setOnCheckedChangeListener( this );
		
		swSimpleList = (Switch) findViewById( R.id.swSimpleList );
		swSimpleList.setOnCheckedChangeListener( this );
		
		swExitAppWhenCloseProtectedColumn = (Switch) findViewById( R.id.swExitAppWhenCloseProtectedColumn );
		swExitAppWhenCloseProtectedColumn.setOnCheckedChangeListener( this );
		
		swShowFollowButtonInButtonBar = (Switch) findViewById( R.id.swShowFollowButtonInButtonBar );
		swShowFollowButtonInButtonBar.setOnCheckedChangeListener( this );
		
		swDontRound = (Switch) findViewById( R.id.swDontRound );
		swDontRound.setOnCheckedChangeListener( this );
		
		swDontUseStreaming = (Switch) findViewById( R.id.swDontUseStreaming );
		swDontUseStreaming.setOnCheckedChangeListener( this );
		
		swDontRefreshOnResume = (Switch) findViewById( R.id.swDontRefreshOnResume );
		swDontRefreshOnResume.setOnCheckedChangeListener( this );
		
		swDontScreenOff = (Switch) findViewById( R.id.swDontScreenOff );
		swDontScreenOff.setOnCheckedChangeListener( this );
		
		swDisableTabletMode = (Switch) findViewById( R.id.swDisableTabletMode );
		swDisableTabletMode.setOnCheckedChangeListener( this );
		
		cbNotificationSound = (CheckBox) findViewById( R.id.cbNotificationSound );
		cbNotificationVibration = (CheckBox) findViewById( R.id.cbNotificationVibration );
		cbNotificationLED = (CheckBox) findViewById( R.id.cbNotificationLED );
		cbNotificationSound.setOnCheckedChangeListener( this );
		cbNotificationVibration.setOnCheckedChangeListener( this );
		cbNotificationLED.setOnCheckedChangeListener( this );
		
		{
			String[] caption_list = new String[]{
				getString( R.string.ask_always ),
				getString( R.string.close_column ),
				getString( R.string.open_column_list ),
				getString( R.string.app_exit ),
			};
			ArrayAdapter< String > adapter = new ArrayAdapter<>( this, android.R.layout.simple_spinner_item, caption_list );
			adapter.setDropDownViewResource( R.layout.lv_spinner_dropdown );
			spBackButtonAction = (Spinner) findViewById( R.id.spBackButtonAction );
			spBackButtonAction.setAdapter( adapter );
			spBackButtonAction.setOnItemSelectedListener( this );
		}
		
		{
			String[] caption_list = new String[]{
				getString( R.string.theme_light ),
				getString( R.string.theme_dark ),
			};
			ArrayAdapter< String > adapter = new ArrayAdapter<>( this, android.R.layout.simple_spinner_item, caption_list );
			adapter.setDropDownViewResource( R.layout.lv_spinner_dropdown );
			spUITheme = (Spinner) findViewById( R.id.spUITheme );
			spUITheme.setAdapter( adapter );
			spUITheme.setOnItemSelectedListener( this );
		}
		{
			String[] caption_list = new String[]{
				getString( R.string.dont_resize ),
				getString( R.string.long_side_pixel, 640 ),
				getString( R.string.long_side_pixel, 800 ),
				getString( R.string.long_side_pixel, 1024 ),
				getString( R.string.long_side_pixel, 1280 ),
				//// サーバ側でさらに縮小されるようなので、1280より上は用意しない
				//	Integer.toString( 1600 ),
				//	Integer.toString( 2048 ),
			};
			ArrayAdapter< String > adapter = new ArrayAdapter<>( this, android.R.layout.simple_spinner_item, caption_list );
			adapter.setDropDownViewResource( R.layout.lv_spinner_dropdown );
			spResizeImage = (Spinner) findViewById( R.id.spResizeImage );
			spResizeImage.setAdapter( adapter );
			spResizeImage.setOnItemSelectedListener( this );
		}
		
		{
			String[] caption_list = new String[]{
				getString( R.string.refresh_scroll_to_toot ),
				getString( R.string.refresh_no_scroll ),
				getString( R.string.dont_refresh ),
			};
			ArrayAdapter< String > adapter = new ArrayAdapter<>( this, android.R.layout.simple_spinner_item, caption_list );
			adapter.setDropDownViewResource( R.layout.lv_spinner_dropdown );
			spRefreshAfterToot = (Spinner) findViewById( R.id.spRefreshAfterToot );
			spRefreshAfterToot.setAdapter( adapter );
			spRefreshAfterToot.setOnItemSelectedListener( this );
		}
		
		findViewById( R.id.btnFooterBackgroundEdit ).setOnClickListener( this );
		findViewById( R.id.btnFooterBackgroundReset ).setOnClickListener( this );
		findViewById( R.id.btnFooterForegroundColorEdit ).setOnClickListener( this );
		findViewById( R.id.btnFooterForegroundColorReset ).setOnClickListener( this );
		findViewById( R.id.btnTabBackgroundColorEdit ).setOnClickListener( this );
		findViewById( R.id.btnTabBackgroundColorReset ).setOnClickListener( this );
		findViewById( R.id.btnTabDividerColorEdit ).setOnClickListener( this );
		findViewById( R.id.btnTabDividerColorReset ).setOnClickListener( this );
		
		ivFooterToot = (ImageView) findViewById( R.id.ivFooterToot );
		ivFooterMenu = (ImageView) findViewById( R.id.ivFooterMenu );
		llFooterBG = findViewById( R.id.llFooterBG );
		vFooterDivider1 = findViewById( R.id.vFooterDivider1 );
		vFooterDivider2 = findViewById( R.id.vFooterDivider2 );
		
		etColumnWidth = (EditText) findViewById( R.id.etColumnWidth );
		etMediaThumbHeight = (EditText) findViewById( R.id.etMediaThumbHeight );
		
	}
	
	boolean load_busy;
	
	private void loadUIFromData(){
		load_busy = true;
		
		swDontConfirmBeforeCloseColumn.setChecked( pref.getBoolean( Pref.KEY_DONT_CONFIRM_BEFORE_CLOSE_COLUMN, false ) );
		swPriorLocalURL.setChecked( pref.getBoolean( Pref.KEY_PRIOR_LOCAL_URL, false ) );
		swDisableFastScroller.setChecked( pref.getBoolean( Pref.KEY_DISABLE_FAST_SCROLLER, true ) );
		swSimpleList.setChecked( pref.getBoolean( Pref.KEY_SIMPLE_LIST, false ) );
		swExitAppWhenCloseProtectedColumn.setChecked( pref.getBoolean( Pref.KEY_EXIT_APP_WHEN_CLOSE_PROTECTED_COLUMN, false ) );
		swShowFollowButtonInButtonBar.setChecked( pref.getBoolean( Pref.KEY_SHOW_FOLLOW_BUTTON_IN_BUTTON_BAR, false ) );
		swDontRound.setChecked( pref.getBoolean( Pref.KEY_DONT_ROUND, false ) );
		swDontUseStreaming.setChecked( pref.getBoolean( Pref.KEY_DONT_USE_STREAMING, false ) );
		swDontRefreshOnResume.setChecked( pref.getBoolean( Pref.KEY_DONT_REFRESH_ON_RESUME, false ) );
		swDontScreenOff.setChecked( pref.getBoolean( Pref.KEY_DONT_SCREEN_OFF, false ) );
		swDisableTabletMode.setChecked( pref.getBoolean( Pref.KEY_DISABLE_TABLET_MODE, false ) );
		
		cbNotificationSound.setChecked( pref.getBoolean( Pref.KEY_NOTIFICATION_SOUND, true ) );
		cbNotificationVibration.setChecked( pref.getBoolean( Pref.KEY_NOTIFICATION_VIBRATION, true ) );
		cbNotificationLED.setChecked( pref.getBoolean( Pref.KEY_NOTIFICATION_LED, true ) );
		
		spBackButtonAction.setSelection( pref.getInt( Pref.KEY_BACK_BUTTON_ACTION, 0 ) );
		spUITheme.setSelection( pref.getInt( Pref.KEY_UI_THEME, 0 ) );
		spResizeImage.setSelection( pref.getInt( Pref.KEY_RESIZE_IMAGE, 4 ) );
		spRefreshAfterToot.setSelection( pref.getInt( Pref.KEY_REFRESH_AFTER_TOOT, 0 ) );
		
		footer_button_bg_color = pref.getInt( Pref.KEY_FOOTER_BUTTON_BG_COLOR, 0 );
		footer_button_fg_color = pref.getInt( Pref.KEY_FOOTER_BUTTON_FG_COLOR, 0 );
		footer_tab_bg_color = pref.getInt( Pref.KEY_FOOTER_TAB_BG_COLOR, 0 );
		footer_tab_divider_color = pref.getInt( Pref.KEY_FOOTER_TAB_DIVIDER_COLOR, 0 );
		
		etColumnWidth.setText( pref.getString( Pref.KEY_COLUMN_WIDTH, "" ) );
		etMediaThumbHeight.setText( pref.getString( Pref.KEY_MEDIA_THUMB_HEIGHT, "" ) );
		
		etColumnWidth.addTextChangedListener( this );
		etMediaThumbHeight.addTextChangedListener( this );
		
		load_busy = false;
		
		showFooterColor();
	}
	
	private void saveUIToData(){
		if( load_busy ) return;
		pref.edit()
			.putBoolean( Pref.KEY_DONT_CONFIRM_BEFORE_CLOSE_COLUMN, swDontConfirmBeforeCloseColumn.isChecked() )
			.putBoolean( Pref.KEY_PRIOR_LOCAL_URL, swPriorLocalURL.isChecked() )
			.putBoolean( Pref.KEY_DISABLE_FAST_SCROLLER, swDisableFastScroller.isChecked() )
			.putBoolean( Pref.KEY_SIMPLE_LIST, swSimpleList.isChecked() )
			.putBoolean( Pref.KEY_EXIT_APP_WHEN_CLOSE_PROTECTED_COLUMN, swExitAppWhenCloseProtectedColumn.isChecked() )
			.putBoolean( Pref.KEY_SHOW_FOLLOW_BUTTON_IN_BUTTON_BAR, swShowFollowButtonInButtonBar.isChecked() )
			.putBoolean( Pref.KEY_DONT_ROUND, swDontRound.isChecked() )
			.putBoolean( Pref.KEY_DONT_USE_STREAMING, swDontUseStreaming.isChecked() )
			.putBoolean( Pref.KEY_DONT_REFRESH_ON_RESUME, swDontRefreshOnResume.isChecked() )
			.putBoolean( Pref.KEY_DONT_SCREEN_OFF, swDontScreenOff.isChecked() )
			.putBoolean( Pref.KEY_DISABLE_TABLET_MODE, swDisableTabletMode.isChecked() )
			
			.putBoolean( Pref.KEY_NOTIFICATION_SOUND, cbNotificationSound.isChecked() )
			.putBoolean( Pref.KEY_NOTIFICATION_VIBRATION, cbNotificationVibration.isChecked() )
			.putBoolean( Pref.KEY_NOTIFICATION_LED, cbNotificationLED.isChecked() )
			
			.putInt( Pref.KEY_BACK_BUTTON_ACTION, spBackButtonAction.getSelectedItemPosition() )
			.putInt( Pref.KEY_UI_THEME, spUITheme.getSelectedItemPosition() )
			.putInt( Pref.KEY_RESIZE_IMAGE, spResizeImage.getSelectedItemPosition() )
			.putInt( Pref.KEY_REFRESH_AFTER_TOOT, spRefreshAfterToot.getSelectedItemPosition() )
			
			.putInt( Pref.KEY_FOOTER_BUTTON_BG_COLOR, footer_button_bg_color )
			.putInt( Pref.KEY_FOOTER_BUTTON_FG_COLOR, footer_button_fg_color )
			.putInt( Pref.KEY_FOOTER_TAB_BG_COLOR, footer_tab_bg_color )
			.putInt( Pref.KEY_FOOTER_TAB_DIVIDER_COLOR, footer_tab_divider_color )
			
			.putString( Pref.KEY_COLUMN_WIDTH, etColumnWidth.getText().toString().trim() )
			.putString( Pref.KEY_MEDIA_THUMB_HEIGHT, etMediaThumbHeight.getText().toString().trim() )
			
			.apply();
		
	}
	
	@Override public void onCheckedChanged( CompoundButton buttonView, boolean isChecked ){
		saveUIToData();
	}
	
	@Override
	public void onItemSelected( AdapterView< ? > parent, View view, int position, long id ){
		saveUIToData();
	}
	
	@Override public void onNothingSelected( AdapterView< ? > parent ){
	}
	
	static final int COLOR_DIALOG_ID_FOOTER_BUTTON_BG = 1;
	static final int COLOR_DIALOG_ID_FOOTER_BUTTON_FG = 2;
	static final int COLOR_DIALOG_ID_FOOTER_TAB_BG = 3;
	static final int COLOR_DIALOG_ID_FOOTER_TAB_DIVIDER = 4;
	
	@Override public void onClick( View v ){
		switch( v.getId() ){
		
		case R.id.btnFooterBackgroundEdit:
			openColorPicker( COLOR_DIALOG_ID_FOOTER_BUTTON_BG, footer_button_bg_color );
			break;
		
		case R.id.btnFooterBackgroundReset:
			footer_button_bg_color = 0;
			saveUIToData();
			showFooterColor();
			break;
		
		case R.id.btnFooterForegroundColorEdit:
			openColorPicker( COLOR_DIALOG_ID_FOOTER_BUTTON_FG, footer_button_fg_color );
			break;
		
		case R.id.btnFooterForegroundColorReset:
			footer_button_fg_color = 0;
			saveUIToData();
			showFooterColor();
			break;
		
		case R.id.btnTabBackgroundColorEdit:
			openColorPicker( COLOR_DIALOG_ID_FOOTER_TAB_BG, footer_tab_bg_color );
			break;
		
		case R.id.btnTabBackgroundColorReset:
			footer_tab_bg_color = 0;
			saveUIToData();
			showFooterColor();
			break;
		
		case R.id.btnTabDividerColorEdit:
			openColorPicker( COLOR_DIALOG_ID_FOOTER_TAB_DIVIDER, footer_tab_divider_color );
			break;
		
		case R.id.btnTabDividerColorReset:
			footer_tab_divider_color = 0;
			saveUIToData();
			showFooterColor();
			break;
			
		}
	}
	
	void openColorPicker( int id, int color ){
		ColorPickerDialog.Builder builder = ColorPickerDialog.newBuilder()
			.setDialogType( ColorPickerDialog.TYPE_CUSTOM )
			.setAllowPresets( true )
			.setShowAlphaSlider( false )
			.setDialogId( id );
		if( color != 0 ) builder.setColor( color );
		builder.show( this );
	}
	
	@Override public void onColorSelected( int dialogId, @ColorInt int color ){
		switch( dialogId ){
		
		case COLOR_DIALOG_ID_FOOTER_BUTTON_BG:
			footer_button_bg_color = 0xff000000 | color;
			saveUIToData();
			showFooterColor();
			break;
		
		case COLOR_DIALOG_ID_FOOTER_BUTTON_FG:
			footer_button_fg_color = 0xff000000 | color;
			saveUIToData();
			showFooterColor();
			break;
		
		case COLOR_DIALOG_ID_FOOTER_TAB_BG:
			footer_tab_bg_color = 0xff000000 | color;
			saveUIToData();
			showFooterColor();
			break;
		
		case COLOR_DIALOG_ID_FOOTER_TAB_DIVIDER:
			footer_tab_divider_color = 0xff000000 | color;
			saveUIToData();
			showFooterColor();
			break;
			
		}
	}
	
	@Override public void onDialogDismissed( int dialogId ){
	}
	
	private void showFooterColor(){
		
		int c = footer_button_bg_color;
		if( c == 0 ){
			ivFooterToot.setBackgroundResource( R.drawable.btn_bg_ddd );
			ivFooterMenu.setBackgroundResource( R.drawable.btn_bg_ddd );
		}else{
			int fg = ( footer_button_fg_color != 0
				? footer_button_fg_color
				: Styler.getAttributeColor( this, R.attr.colorRippleEffect ) );
			ViewCompat.setBackground( ivFooterToot, Styler.getAdaptiveRippleDrawable( c, fg ) );
			ViewCompat.setBackground( ivFooterMenu, Styler.getAdaptiveRippleDrawable( c, fg ) );
		}
		
		c = footer_button_fg_color;
		if( c == 0 ){
			Styler.setIconDefaultColor( this, ivFooterToot, R.attr.ic_edit );
			Styler.setIconDefaultColor( this, ivFooterMenu, R.attr.ic_hamburger );
		}else{
			Styler.setIconCustomColor( this, ivFooterToot, c, R.attr.ic_edit );
			Styler.setIconCustomColor( this, ivFooterMenu, c, R.attr.ic_hamburger );
		}
		
		c = footer_tab_bg_color;
		if( c == 0 ){
			llFooterBG.setBackgroundColor( Styler.getAttributeColor( this, R.attr.colorColumnStripBackground ) );
		}else{
			llFooterBG.setBackgroundColor( c );
		}
		
		c = footer_tab_divider_color;
		if( c == 0 ){
			vFooterDivider1.setBackgroundColor( Styler.getAttributeColor( this, R.attr.colorImageButton ) );
			vFooterDivider2.setBackgroundColor( Styler.getAttributeColor( this, R.attr.colorImageButton ) );
		}else{
			vFooterDivider1.setBackgroundColor( c );
			vFooterDivider2.setBackgroundColor( c );
		}
	}
	
	@Override public void beforeTextChanged( CharSequence s, int start, int count, int after ){
		
	}
	
	@Override public void onTextChanged( CharSequence s, int start, int before, int count ){
		
	}
	
	@Override public void afterTextChanged( Editable s ){
		saveUIToData();
	}
}
