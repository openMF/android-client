package com.mifos.api.local.databasehelper;

import com.mifos.objects.group.CenterInfo;
import com.mifos.objects.group.CenterInfo_Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.functions.Func0;

/**
 * Created by mayankjindal on 09/08/17.
 */
@Singleton
public class DatabaseHelperRunReport {

    @Inject
    public DatabaseHelperRunReport() {
    }

    /**
     * This Method Fetch the Center Summary that are attached to the Center.
     * @param centerId Center Id
     * @return List<CenterInfo>
     */
    public Observable<List<CenterInfo>> readCenterSummaryInfo(final int centerId) {
        return Observable.defer(new Func0<Observable<List<CenterInfo>>>() {
            @Override
            public Observable<List<CenterInfo>> call() {

                List<CenterInfo> centerInfos = SQLite.select()
                        .from(CenterInfo.class)
                        .where(CenterInfo_Table.id.eq(centerId))
                        .queryList();

                return Observable.just(centerInfos);
            }
        });
    }

    /**
     * This Method  write the Center Summary in tho DB. According to Schema Defined in Model
     * @param centerInfos Model of List of Center Info
     * @param centerId       Center Id
     * @return List<CenterInfo>
     */
    public Observable<List<CenterInfo>> saveCenterSummaryInfo(final List<CenterInfo> centerInfos,
                                              final int centerId) {
        return Observable.defer(new Func0<Observable<List<CenterInfo>>>() {
            @Override
            public Observable<List<CenterInfo>> call() {
                for (CenterInfo centerInfo : centerInfos) {
                    centerInfo.setId(centerId);
                    centerInfo.save();
                }
                return Observable.just(centerInfos);
            }
        });
    }
}
