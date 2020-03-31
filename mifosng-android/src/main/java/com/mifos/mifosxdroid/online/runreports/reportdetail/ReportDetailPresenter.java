package com.mifos.mifosxdroid.online.runreports.reportdetail;

import com.mifos.api.datamanager.DataManagerRunReport;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.objects.runreports.DataRow;
import com.mifos.objects.runreports.FullParameterListResponse;
import com.mifos.utils.MFErrorParser;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.plugins.RxJavaPlugins;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Tarun on 04-08-17.
 */

public class ReportDetailPresenter extends BasePresenter<ReportDetailMvpView> {

    private DataManagerRunReport dataManager;
    private CompositeSubscription subscription;

    @Inject
    public ReportDetailPresenter(DataManagerRunReport dataManager) {
        this.dataManager = dataManager;
        subscription = new CompositeSubscription();
    }

    @Override
    public void attachView(ReportDetailMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (subscription != null) {
            subscription.unsubscribe();
        }
    }

    public void fetchFullParameterList(final String reportName, boolean parameterType) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        subscription.add(dataManager.getReportFullParameterList(reportName, parameterType)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<FullParameterListResponse>() {
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
                    public void onNext(FullParameterListResponse response) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showFullParameterResponse(response);
                    }
                }));
    }

    public void fetchParameterDetails(final String parameterName, boolean parameterType) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        subscription.add(dataManager.getReportParameterDetails(parameterName, parameterType)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<FullParameterListResponse>() {
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
                                getMvpView().showError(MFErrorParser.parseError(errorMessage)
                                        .getErrors().get(0).getDeveloperMessage());
                            }
                        } catch (Throwable throwable) {
                            RxJavaPlugins.getInstance().getErrorHandler().handleError(e);
                        }
                    }

                    @Override
                    public void onNext(FullParameterListResponse response) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showParameterDetails(response, parameterName);
                    }
                }));
    }

    public void fetchOffices(final String parameterName, int officeId, boolean parameterType) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        subscription.add(dataManager.getRunReportOffices(parameterName, officeId, parameterType)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<FullParameterListResponse>() {
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
                                getMvpView().showError(MFErrorParser.parseError(errorMessage)
                                        .getErrors().get(0).getDeveloperMessage());
                            }
                        } catch (Throwable throwable) {
                            RxJavaPlugins.getInstance().getErrorHandler().handleError(e);
                        }
                    }

                    @Override
                    public void onNext(FullParameterListResponse response) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showOffices(response, parameterName);
                    }
                }));
    }

    public void fetchProduct(final String parameterName, String currencyId, boolean parameterType) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        subscription.add(dataManager.getRunReportProduct(parameterName, currencyId, parameterType)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<FullParameterListResponse>() {
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
                                getMvpView().showError(MFErrorParser.parseError(errorMessage)
                                        .getErrors().get(0).getDeveloperMessage());
                            }
                        } catch (Throwable throwable) {
                            RxJavaPlugins.getInstance().getErrorHandler().handleError(e);
                        }
                    }

                    @Override
                    public void onNext(FullParameterListResponse response) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showProduct(response, parameterName);
                    }
                }));
    }

    public void fetchRunReportWithQuery(String reportName, Map<String, String> options) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        subscription.add(dataManager.getRunReportWithQuery(reportName, options)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<FullParameterListResponse>() {
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
                                getMvpView().showError(new JSONObject(errorMessage)
                                        .getString("developerMessage"));
                            }
                        } catch (Throwable throwable) {
                            RxJavaPlugins.getInstance().getErrorHandler().handleError(e);
                        }
                    }

                    @Override
                    public void onNext(FullParameterListResponse response) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showRunReport(response);
                    }
                }));
    }

    /**
     * Method to filter Values of spinners with associated integer codes
     *
     * @param response List of DataRows
     * @param values   List of corresponding String values to be shown in Spinner
     * @return HashMap of String values with the corresponding integer code
     */
    public HashMap<String, Integer> filterIntHashMapForSpinner(List<DataRow> response,
                                                               final ArrayList<String> values) {
        final HashMap<String, Integer> map = new HashMap<>();
        values.clear();
        Observable.from(response)
                .subscribe(new Action1<DataRow>() {
                    @Override
                    public void call(DataRow dataRow) {
                        Integer id = Integer.parseInt(dataRow.getRow().get(0));
                        String value = dataRow.getRow().get(1);
                        values.add(value);
                        map.put(value, id);
                    }
                });
        return map;
    }

    /**
     * Method to filter out values for the Spinners.
     *
     * @param response List of DataRows
     * @param values   List of the corresponding values
     * @return HashMap of value and currency code pairs.
     */
    public HashMap<String, String> filterStringHashMapForSpinner(List<DataRow> response,
                                                                 final ArrayList<String> values) {
        final HashMap<String, String> map = new HashMap<>();
        values.clear();
        Observable.from(response)
                .subscribe(new Action1<DataRow>() {
                    @Override
                    public void call(DataRow dataRow) {
                        String code = dataRow.getRow().get(0);
                        String value = dataRow.getRow().get(1);
                        values.add(value);
                        map.put(value, code);
                    }
                });
        return map;
    }

}
