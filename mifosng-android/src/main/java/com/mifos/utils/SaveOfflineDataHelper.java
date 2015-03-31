/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.utils;


import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.mifos.objects.db.CollectionMeetingCalendar;
import com.mifos.objects.db.CollectionSheet;
import com.mifos.objects.db.EntityType;
import com.mifos.objects.db.MeetingCenter;
import com.mifos.objects.db.MeetingDate;
import com.mifos.objects.db.OfflineCenter;
import com.mifos.objects.db.Status;
import com.mifos.services.data.Payload;
import com.orm.query.Select;

import java.util.HashMap;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SaveOfflineDataHelper {

    private static final HashMap<Long, Integer> syncState;
    private Context mContext;
    private OfflineDataSaveListener offlineDataSaveListener;
    private String tag = getClass().getSimpleName();
    private int centerCount = 0;

    static {
        syncState = new HashMap<Long, Integer>();
    }

    public SaveOfflineDataHelper(Context context) {
        this.mContext = context;
    }

    public void setOfflineDataSaveListener(OfflineDataSaveListener offlineDataSaveListener) {
        this.offlineDataSaveListener = offlineDataSaveListener;
    }

    private HashMap<Long, Integer> saveSyncState() {
        syncState.clear();
        List<MeetingCenter> listCenters = Select.from(MeetingCenter.class).list();
        for (MeetingCenter center : listCenters)
            syncState.put(center.getCenterId(), center.getIsSynced());
        return syncState;
    }

    public void saveOfflineCenterData(Context context, OfflineCenter center) {
        saveSyncState();
        try {
            MeetingDate.deleteAll(MeetingDate.class);
            Status.deleteAll(Status.class);
            EntityType.deleteAll(EntityType.class);
            CollectionMeetingCalendar.deleteAll(CollectionMeetingCalendar.class);
            MeetingCenter.deleteAll(MeetingCenter.class);
            OfflineCenter.deleteAll(OfflineCenter.class);
        } catch (Exception ex) {
        }

        MeetingCenter[] meetingCenterList = center.getMeetingFallCenters();
        for (MeetingCenter meetingCenter : meetingCenterList) {
            List<Integer> activationDate = meetingCenter.getActivationDate();

            MeetingDate meetingDate = new MeetingDate();
            meetingDate.setYear(activationDate.get(0));
            meetingDate.setMonth(activationDate.get(1));
            meetingDate.setDay(activationDate.get(2));
            meetingDate.save();

            Status status = new Status();
            status.setCode(meetingCenter.getStatus().getCode());
            status.setValue(meetingCenter.getStatus().getValue());
            status.save();
            ;

            CollectionMeetingCalendar collectionMeetingCalendar = meetingCenter.getCollectionMeetingCalendar();

            EntityType entityType = new EntityType();
            entityType.setCode(collectionMeetingCalendar.getEntityType().getCode());
            entityType.setValue(collectionMeetingCalendar.getEntityType().getValue());
            entityType.save();

            MeetingDate calendarMeetingDate = new MeetingDate();
            List<Integer> calendarDates = collectionMeetingCalendar.getStartDate();
            calendarMeetingDate.setYear(calendarDates.get(0));
            calendarMeetingDate.setMonth(calendarDates.get(1));
            calendarMeetingDate.setDay(calendarDates.get(2));
            calendarMeetingDate.save();

            CollectionMeetingCalendar collectionMeetingCalendar1 = new CollectionMeetingCalendar();
            collectionMeetingCalendar1.setMeetingCalendarDate(calendarMeetingDate);
            collectionMeetingCalendar1.setCalendarInstanceId(collectionMeetingCalendar.getCalendarInstanceId());
            collectionMeetingCalendar1.setDescription(collectionMeetingCalendar.getDescription());
            collectionMeetingCalendar1.setEntityId(collectionMeetingCalendar.getEntityId());
            collectionMeetingCalendar1.setLocation(collectionMeetingCalendar.getLocation());
            collectionMeetingCalendar1.setRecurrence(collectionMeetingCalendar.getRecurrence());
            collectionMeetingCalendar1.setRepeating(collectionMeetingCalendar.isRepeating());
            collectionMeetingCalendar1.setTitle(collectionMeetingCalendar.getTitle());
            collectionMeetingCalendar1.setEntityType(entityType);
            collectionMeetingCalendar1.setCalendarId(collectionMeetingCalendar.getId());
            collectionMeetingCalendar1.save();

            MeetingCenter meetingCenter1 = new MeetingCenter();
            meetingCenter1.setMeetingDate(meetingDate);
            if (syncState.get(meetingCenter.getId()) != null)
                meetingCenter1.setIsSynced(syncState.get(meetingCenter.getId()));
            else
                meetingCenter1.setIsSynced(meetingCenter.getIsSynced());

            meetingCenter1.setStatus(status);
            meetingCenter1.setActive(meetingCenter.isActive());
            meetingCenter1.setExternalId(meetingCenter.getExternalId());
            meetingCenter1.setName(meetingCenter.getName());
            meetingCenter1.setOfficeId(meetingCenter.getOfficeId());
            meetingCenter1.setStaffId(meetingCenter.getStaffId());
            meetingCenter1.setCollectionMeetingCalendar(collectionMeetingCalendar1);
            meetingCenter1.setStaffName(meetingCenter.getStaffName());
            meetingCenter1.setCenterId(meetingCenter.getId());
            meetingCenter1.save();
        }
        center.save();
        if (meetingCenterList.length > 0)
            getGroupsData(context, meetingCenterList);
        Log.i(tag, "--------All Offline Center Saved to DB---------");

        if (offlineDataSaveListener != null) {
            offlineDataSaveListener.dataSaved();
        }
    }

    private Payload getPayload(Context context, MeetingCenter center) {
        final Payload payload = new Payload();
        payload.setTransactionDate(DateHelper.getPayloadDate(context));
            payload.setCalendarId(center.getCollectionMeetingCalendar().getCalendarInstanceId());
        return payload;
    }

    private void getGroupsData(final Context context, final MeetingCenter[] meetingCenterList) {

        if (Network.isOnline(context)) {
            MeetingCenter center = meetingCenterList[centerCount];
            final long centerId = center.getId();
            Log.i(tag, "Fetching Group data for Center Id:" + centerId);
            ((MifosApplication) context.getApplicationContext()).api.centerService.getCollectionSheet(centerId, getPayload(context, center), new Callback<CollectionSheet>() {
                @Override
                public void success(CollectionSheet collectionSheet, Response arg1) {
                    if (collectionSheet != null) {
                        collectionSheet.saveData(centerId);
                        centerCount++;
                        if (centerCount < meetingCenterList.length)
                            getGroupsData(context, meetingCenterList);
                    }
                }

                @Override
                public void failure(RetrofitError arg0) {
                    Toast.makeText(context, "There was some error fetching data.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public interface OfflineDataSaveListener {
        public void dataSaved();
    }
}
