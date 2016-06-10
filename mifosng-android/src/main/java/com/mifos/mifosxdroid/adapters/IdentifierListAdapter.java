/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.mifos.api.BaseApiManager;
import com.mifos.api.DataManager;
import com.mifos.api.GenericResponse;
import com.mifos.mifosxdroid.R;
import com.mifos.objects.noncore.Identifier;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.BindView;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by ishankhanna on 03/07/14.
 */
public class IdentifierListAdapter extends BaseAdapter {

    public final String LOG_TAG = getClass().getSimpleName();

    Context context;

    LayoutInflater layoutInflater;

    List<Identifier> identifiers = new ArrayList<Identifier>();

    int clientId;

    DataManager dataManager;
    BaseApiManager baseApiManager;


    public IdentifierListAdapter(Context context, List<Identifier> identifierList, int clientId) {

        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        identifiers = identifierList;
        this.clientId = clientId;
        baseApiManager = new BaseApiManager();
        dataManager = new DataManager(baseApiManager);
    }

    @Override
    public int getCount() {
        return identifiers.size();
    }

    @Override
    public Identifier getItem(int i) {
        return identifiers.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ReusableIdentifierViewHolder reusableIdentifierViewHolder;
        if (view == null) {

            view = layoutInflater.inflate(R.layout.row_identifier_list, null);
            reusableIdentifierViewHolder = new ReusableIdentifierViewHolder(view);
            view.setTag(reusableIdentifierViewHolder);

        } else {
            reusableIdentifierViewHolder = (ReusableIdentifierViewHolder) view.getTag();
        }

        final Identifier identifier = identifiers.get(i);
        reusableIdentifierViewHolder.tv_identifier_id.setText(String.valueOf(identifier.getId()));
        reusableIdentifierViewHolder.tv_identifier_descrption.setText(identifier.getDescription()
                == null ? "" : identifier.getDescription());
        reusableIdentifierViewHolder.tv_identifier_type.setText(identifier.getDocumentType()
                .getName());

        reusableIdentifierViewHolder.bt_delete_identifier.setOnClickListener(new View
                .OnClickListener() {
            @Override
            public void onClick(View view) {

                Observable<GenericResponse> call = dataManager.deleteIdentifier(clientId,
                        identifier.getId());
                Subscription subscription = call.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<GenericResponse>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.d(getClass().getSimpleName(), e.getMessage());
                            }

                            @Override
                            public void onNext(GenericResponse genericResponse) {
                                Log.d(LOG_TAG, genericResponse.toString());
                            }
                        });

            }
        });

        return view;
    }

    public static class ReusableIdentifierViewHolder {

        @BindView(R.id.tv_identifier_id)
        TextView tv_identifier_id;
        @BindView(R.id.tv_identifier_descrption)
        TextView tv_identifier_descrption;
        @BindView(R.id.tv_identifier_type)
        TextView tv_identifier_type;
        @BindView(R.id.tv_identifier_document)
        TextView tv_identifier_document;
        @BindView(R.id.bt_move_identifier)
        Button bt_move_identifier;
        @BindView(R.id.bt_delete_identifier)
        Button bt_delete_identifier;

        public ReusableIdentifierViewHolder(View view) {
            ButterKnife.bind(this, view);
        }


    }
}
