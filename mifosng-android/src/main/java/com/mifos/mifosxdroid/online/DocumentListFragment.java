package com.mifos.mifosxdroid.online;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.adapters.DocumentListAdapter;
import com.mifos.objects.noncore.Document;
import com.mifos.services.API;
import com.mifos.services.GenericResponse;
import com.mifos.utils.AsyncFileDownloader;
import com.mifos.utils.Constants;
import com.mifos.utils.FileUtils;
import com.mifos.utils.SafeUIBlockingUtility;

import java.io.File;
import java.net.URISyntaxException;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

public class DocumentListFragment extends Fragment {

    public static final int MENU_ITEM_ADD_NEW_DOCUMENT = 1000;

    private static final int FILE_SELECT_CODE = 0;

    @InjectView(R.id.lv_documents)
    ListView lv_documents;

    View rootView;

    SafeUIBlockingUtility safeUIBlockingUtility;

    ActionBarActivity activity;

    SharedPreferences sharedPreferences;

    ActionBar actionBar;

    private OnFragmentInteractionListener mListener;

    private String entityType;
    private int entityId;

    public DocumentListFragment() {
        // Required empty public constructor
    }

    public static DocumentListFragment newInstance(String entityType, int entiyId) {
        DocumentListFragment fragment = new DocumentListFragment();
        Bundle args = new Bundle();
        args.putString(Constants.ENTITY_TYPE, entityType);
        args.putInt(Constants.ENTITY_ID, entiyId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

            entityType = getArguments().getString(Constants.ENTITY_TYPE);
            entityId = getArguments().getInt(Constants.ENTITY_ID);

        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_document_list, container, false);
        activity = (ActionBarActivity) getActivity();
        safeUIBlockingUtility = new SafeUIBlockingUtility(getActivity());
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        actionBar = activity.getSupportActionBar();
        ButterKnife.inject(this, rootView);

        inflateDocumentList();

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
//            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        menu.clear();

        MenuItem menuItemAddNewDocument = menu.add(Menu.NONE, MENU_ITEM_ADD_NEW_DOCUMENT, Menu.NONE, getString(R.string.add_new));
        menuItemAddNewDocument.setIcon(getResources().getDrawable(R.drawable.ic_action_content_new));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            menuItemAddNewDocument.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        }

        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == MENU_ITEM_ADD_NEW_DOCUMENT) {

            openFilePicker();
        }

        return super.onOptionsItemSelected(item);
    }

    public void inflateDocumentList() {

        safeUIBlockingUtility.safelyBlockUI();

        API.documentService.getListOfDocuments(entityType, entityId, new Callback<List<Document>>() {
            @Override
            public void success(final List<Document> documents, Response response) {

                if (documents != null) {

                    for (Document document : documents) {

                        Log.w(document.getFileName(), document.getSize() + " bytes");

                    }

                    DocumentListAdapter documentListAdapter = new DocumentListAdapter(getActivity(), documents);
                    lv_documents.setAdapter(documentListAdapter);

                    lv_documents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            AsyncFileDownloader asyncFileDownloader = new AsyncFileDownloader(getActivity(), documents.get(i).getFileName());
                            asyncFileDownloader.execute(entityType, String.valueOf(entityId), String.valueOf(documents.get(i).getId()));
                        }
                    });

                }

                safeUIBlockingUtility.safelyUnBlockUI();
            }

            @Override
            public void failure(RetrofitError retrofitError) {

                Log.d("Error", retrofitError.getLocalizedMessage());

                safeUIBlockingUtility.safelyUnBlockUI();


            }
        });

    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"),
                    FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(getActivity(), "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
        }
    }


    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Uri uri);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    // Get the Uri of the selected file
                    Uri uri = data.getData();
                    Log.d(getClass().getSimpleName(), "File Uri: " + uri.toString());
                    // Get the path
                    try {
                        String path = FileUtils.getPath(getActivity(), uri);
                        Log.d(getClass().getSimpleName(), "File Path: " + path);

                        uploadFile(path);

                    } catch (URISyntaxException e) {
                        e.printStackTrace();

                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void uploadFile(String path) {

        File file = new File(path);
        System.out.println("File Name :" + file.getName());

        String[] parts = file.getName().split("\\.");
        System.out.println("Extension :"+parts[1]);
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(parts[1]);
        System.out.println("Mime Type = "+mimeType);

        TypedFile typedFile = new TypedFile(mimeType, file);

        safeUIBlockingUtility.safelyBlockUI();
        API.documentService.createDocument(entityType, entityId, file.getName(), "Some Random Description",
                typedFile, new Callback<GenericResponse>() {
                    @Override
                    public void success(GenericResponse genericResponse, Response response) {

                        if (genericResponse != null) {
                            System.out.println(genericResponse.toString());
                        }

                        safeUIBlockingUtility.safelyUnBlockUI();
                    }

                    @Override
                    public void failure(RetrofitError retrofitError) {

                        safeUIBlockingUtility.safelyUnBlockUI();

                    }
                }
        );

    }

}
