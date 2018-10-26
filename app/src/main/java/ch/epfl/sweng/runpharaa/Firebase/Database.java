package ch.epfl.sweng.runpharaa.Firebase;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import ch.epfl.sweng.runpharaa.DatabaseManagement;
import ch.epfl.sweng.runpharaa.tracks.Track;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

public class Database {

    private boolean isTest = true;
    private boolean shouldFail = true;
    private boolean isCancelled = false;


    private String s_tracks = "tracks";
    private String s_key = "key";

    //Should create a track for this?
    private String trackUID = "id";

    //For all mocked objects
    @Mock
    private FirebaseDatabase firebaseDatabaseMock;

    @Mock
    private DatabaseReference databaseReferenceMock;

    @Mock
    private DatabaseReference drTracks;

    @Mock
    private DatabaseReference drTracksPush;

    @Mock
    private DatabaseReference drKey;

    @Mock
    private DatabaseReference drTracksUID;

    @Mock
    private Track track;

    @Mock
    private Task<Void> setValueTrack;

    @Mock
    private ValueEventListener valueEventListener;

    @Mock
    private DataSnapshot snapOnDataChange;

    @Mock
    private DatabaseError snapOnDataError;





    private Database(){

    }

    public FirebaseDatabase getInstance(){
        if(isTest){
            MockitoAnnotations.initMocks(this);
            instanciateDB();
            instanciateDBRef();
            instanciatedrTracks();
            instanciatedrKeys();
            instanciateRead();
            return firebaseDatabaseMock;
        } else {
            return FirebaseDatabase.getInstance();
        }
    }


    private void instanciateDB() {
        when(firebaseDatabaseMock.getReference()).thenReturn(databaseReferenceMock);
    }

    private void instanciateDBRef(){
        when(databaseReferenceMock.child(s_tracks)).thenReturn(drTracks);
        when(databaseReferenceMock.child(s_key)).thenReturn(drKey);
    }


    private void instanciatedrTracks(){
        when(drTracks.push()).thenReturn(drTracksPush);
        when(drTracksPush.getKey()).thenReturn(s_key);

        when(drTracks.child(trackUID)).thenReturn(drTracksUID);
        when(drTracksUID.setValue(track)).thenReturn(setValueTrack);

    }

    private void instanciatedrKeys(){
        when(drKey.setValue(track)).thenReturn(setValueTrack);
        when(setValueTrack.addOnFailureListener(any(OnFailureListener.class))).thenAnswer(new Answer<Task<Void>>() {
            @Override
            public Task<Void> answer(InvocationOnMock invocation) throws Throwable {
                OnFailureListener l = (OnFailureListener) invocation.getArguments()[0];
                if(shouldFail) {
                    l.onFailure(new IllegalStateException("Could not add track to DB"));
                }
                return setValueTrack;
            }
        });
    }

    private void instanciateRead(){
        doAnswer(new Answer<ValueEventListener>() {
            @Override
            public ValueEventListener answer(InvocationOnMock invocation) throws Throwable {
                ValueEventListener l = (ValueEventListener) invocation.getArguments()[0];
                if (isCancelled) {
                    l.onCancelled(snapOnDataError);
                } else {
                    l.onDataChange(snapOnDataChange);
                }
                return l;
            }
        }).when(drTracks).addListenerForSingleValueEvent(any(ValueEventListener.class));


        doAnswer(new Answer<ValueEventListener>() {
            @Override
            public ValueEventListener answer(InvocationOnMock invocation) throws Throwable {
                ValueEventListener l = (ValueEventListener) invocation.getArguments()[0];
                if (isCancelled) {
                    l.onCancelled(snapOnDataError);
                } else {
                    l.onDataChange(snapOnDataChange);
                }
                return l;
            }
        }).when(drKey).addListenerForSingleValueEvent(any(ValueEventListener.class));
    }

}
