package com.mifos.mifosxdroid.online.runreports.reportcategory;


import com.mifos.api.datamanager.DataManagerRunReport;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.objects.runreports.client.ClientReportTypeItem;
import com.mifos.utils.MFErrorParser;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.plugins.RxJavaPlugins;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Tarun on 03-08-17.
 */

public class ReportCategoryPresenter extends BasePresenter<ReportCategoryMvpView> {

    private DataManagerRunReport dataManager;
    private CompositeSubscription subscription;

    @Inject
    public ReportCategoryPresenter(DataManagerRunReport manager) {
        dataManager = manager;
        subscription = new CompositeSubscription();
    }

    @Override
    public void attachView(ReportCategoryMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (subscription != null) {
            subscription.unsubscribe();
        }
    }

    void fetchCategories(String reportCategory, boolean genericResultSet, boolean parameterType) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        subscription.add(dataManager.getReportCategories(reportCategory, genericResultSet,
                parameterType)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<ClientReportTypeItem>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        try {
                            if (e instanceof HttpException) {
                                String errorMessage = ((HttpException) e).response().errorBody()
                                        .string();
                                // Default User message is null in these queries most of the times.
                                // Hence, show Developer message.
                                getMvpView().showError(MFErrorParser.parseError(errorMessage)
                                        .getErrors().get(0).getDeveloperMessage());
                            }
                        } catch (Throwable throwable) {
                            RxJavaPlugins.getInstance().getErrorHandler().handleError(e);
                        }
                    }

                    @Override
                    public void onNext(List<ClientReportTypeItem> clientReportTypeItems) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showReportCategories(filterUniques(clientReportTypeItems));
                    }
                }));
    }

    /**
     * Method to filter unique ClientReportTypeItems from the given list
     *
     * @param list List of ClientReportTypeItems retrieved from server
     * @return List of ClientReportTypeItems with unique items based on their Id.
     */
    private ArrayList<ClientReportTypeItem> filterUniques(List<ClientReportTypeItem> list) {
        Map<Integer, ClientReportTypeItem> map = new LinkedHashMap<>();
        for (ClientReportTypeItem item : list) {
            map.put(item.getReportId(), item);
        }
        ArrayList<ClientReportTypeItem> uniques = new ArrayList<>();
        uniques.addAll(map.values());
        return uniques;
    }
}
