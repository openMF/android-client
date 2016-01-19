/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid;

import android.os.Bundle;

import com.mifos.mifosxdroid.core.BaseActivity;
import com.mifos.mifosxdroid.fragments.GroupFragment;

public class GroupActivity extends BaseActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toolbar_container);
        replaceFragment(new GroupFragment(), false, R.id.container);
    }
}
