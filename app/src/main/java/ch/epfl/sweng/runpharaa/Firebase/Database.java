package ch.epfl.sweng.runpharaa.Firebase;


import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.ContactsContract;

import com.google.android.gms.maps.model.LatLng;
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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import ch.epfl.sweng.runpharaa.CustLatLng;
import ch.epfl.sweng.runpharaa.R;
import ch.epfl.sweng.runpharaa.tracks.Track;
import ch.epfl.sweng.runpharaa.tracks.TrackProperties;
import ch.epfl.sweng.runpharaa.tracks.TrackType;
import ch.epfl.sweng.runpharaa.user.FirebaseUserAdapter;
import ch.epfl.sweng.runpharaa.user.User;
import ch.epfl.sweng.runpharaa.utils.Util;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

public class Database {

    private final static boolean isTest = false;
    private final static boolean shouldFail = true;
    private final static boolean isCancelled = false;
    private final static boolean userExists = false;


    private final static String s_tracks = "tracks";
    private final static String s_key = "key";
    private final static String s_user = "users";
    private final static String s_favorite = "favoriteTracks";
    private final static String s_likes = "likedTracks";
    private final static String s_create = "createdTracks";

    private final static User fake_user = new User("FakeUser", 2000, Uri.parse(""), new LatLng(21.23, 12.112), "1");


    //Should create a track for this?
    private static String trackUID = "id";

    private Track t = new Track();

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
    private DatabaseReference drUser;

    @Mock
    private DatabaseReference drUserAnyChild;

    @Mock
    private DatabaseReference drUserAnyChildFavorites;

    @Mock
    private DatabaseReference drUserAnyChildLikes;

    @Mock
    private DatabaseReference drUserAnyChildCreate;

    @Mock
    private DatabaseReference drUserAnyChildFavoritesChild;

    @Mock
    private DatabaseReference drUserAnyChildLikesChild;

    @Mock
    private DatabaseReference drUserAnyChildCreatesChild;

    @Mock
    private Task<Void> setValueTrack;

    @Mock
    private ValueEventListener valueEventListener;

    @Mock
    private DataSnapshot snapOnDataChangeRead;

    @Mock
    private DatabaseError snapOnDataErrorRead;

    @Mock
    private DataSnapshot snapOnDataChangeUser;

    @Mock
    private DataSnapshot snapOnDataChangeUserChild;

    @Mock
    private DatabaseError snapOnDataErrorUser;


    @Mock
    private DataSnapshot snapInit;


    @Mock
    private DataSnapshot snapInitTrack;

    @Mock
    private Track track;


    private Database() {

    }

    public static FirebaseDatabase getInstance(){
        return (isTest)? new Database().instanciateMock() : FirebaseDatabase.getInstance();
    }

    private FirebaseDatabase instanciateMock(){
        if(isTest){
            MockitoAnnotations.initMocks(this);
            createTrack();
            instanciateDB();
            instanciateDBRef();
            instanciatedrTracks();
            instanciatedrKeys();
            instanciatedrUsers();
            instanciateRead();
            instanciateSnapshots();
            return firebaseDatabaseMock;
        } else {
            return FirebaseDatabase.getInstance();
        }
    }


    private void instanciateDB() {
        when(firebaseDatabaseMock.getReference()).thenReturn(databaseReferenceMock);
    }


    private void instanciateDBRef() {
        when(databaseReferenceMock.child(s_tracks)).thenReturn(drTracks);
        when(databaseReferenceMock.child(s_key)).thenReturn(drKey);
        when(databaseReferenceMock.child(s_user)).thenReturn(drUser);
    }


    private void instanciateSnapshots() {
        //TODO: verifier si on a que ca comme cle
        when(snapInit.child(s_tracks)).thenReturn(snapInitTrack);
        when(snapInitTrack.getValue(Track.class)).thenReturn(t);

        when(snapOnDataChangeUser.child(any(String.class))).thenReturn(snapOnDataChangeUserChild);
        when(snapOnDataChangeUserChild.exists()).thenReturn(userExists);
    }

    private void instanciatedrUsers() {
        when(drUser.child(any(String.class))).thenReturn(drUserAnyChild);

        //write new user
        doAnswer(new Answer<ValueEventListener>() {
            @Override
            public ValueEventListener answer(InvocationOnMock invocation) throws Throwable {
                ValueEventListener l = (ValueEventListener) invocation.getArguments()[0];
                if (isCancelled) {
                    l.onCancelled(snapOnDataErrorUser);
                } else {
                    l.onDataChange(snapOnDataChangeUser);
                }
                return l;
            }
        }).when(drUserAnyChild).addListenerForSingleValueEvent(any(ValueEventListener.class));

        //when(drUserAnyChild.child(any(String.class))).thenReturn(drUserAnyChild);
        /*when(drUserAnyChildId.setValue(any(FirebaseUserAdapter.class))).thenAnswer(new Answer<Task<User>>() {
            @Override
            public Task<User> answer(InvocationOnMock invocation) throws Throwable {
                OnFailureListener l = (OnFailureListener) invocation.getArguments()[0];
                if (shouldFail) {
                    l.onFailure(new IllegalStateException("Could not retrieve User"));
                }
                return ;
            }
        });*/

        when(drUserAnyChild.child(s_favorite)).thenReturn(drUserAnyChildFavorites);
        when(drUserAnyChild.child(s_likes)).thenReturn(drUserAnyChildLikes);
        when(drUserAnyChild.child(s_create)).thenReturn(drUserAnyChildCreate);

        when(drUserAnyChildFavorites.child(any(String.class))).thenReturn(drUserAnyChildFavoritesChild);
        when(drUserAnyChildLikes.child(any(String.class))).thenReturn(drUserAnyChildLikesChild);
        when(drUserAnyChildCreate.child(any(String.class))).thenReturn(drUserAnyChildCreatesChild);

        doAnswer(new Answer<ValueEventListener>() {
            @Override
            public ValueEventListener answer(InvocationOnMock invocation) throws Throwable {
                ValueEventListener l = (ValueEventListener) invocation.getArguments()[0];
                if (isCancelled) {
                    l.onCancelled(snapOnDataErrorRead);
                } else {
                    fake_user.addToFavorites("0");
                }
                return l;
            }
        }).when(drUserAnyChildFavoritesChild).addListenerForSingleValueEvent(any(ValueEventListener.class));
        doAnswer(new Answer<ValueEventListener>() {
            @Override
            public ValueEventListener answer(InvocationOnMock invocation) throws Throwable {
                ValueEventListener l = (ValueEventListener) invocation.getArguments()[0];
                if (isCancelled) {
                    l.onCancelled(snapOnDataErrorRead);
                } else {
                    fake_user.addToCreatedTracks("0");
                }
                return l;
            }
        }).when(drUserAnyChildCreatesChild).addListenerForSingleValueEvent(any(ValueEventListener.class));
        doAnswer(new Answer<ValueEventListener>() {
            @Override
            public ValueEventListener answer(InvocationOnMock invocation) throws Throwable {
                ValueEventListener l = (ValueEventListener) invocation.getArguments()[0];
                if (isCancelled) {
                    l.onCancelled(snapOnDataErrorRead);
                } else {
                    fake_user.like("0");
                }
                return l;
            }
        }).when(drUserAnyChildLikesChild).addListenerForSingleValueEvent(any(ValueEventListener.class));


        //TODO: How to make it
        //when(drUserAnyChildIdFavoritesChild.setValue(any(String.class))).thenReturn();
        //when(drUserAnyChildIdFavoritesChild.removeValue())
        //when(drUserAnyChildCreatesChild.setValue(any(String)))
        //when(drUserAnyChildLikeChild.removeValue())

    }


    private void instanciatedrTracks() {
        when(drTracks.push()).thenReturn(drTracksPush);
        when(drTracksPush.getKey()).thenReturn(s_key);

        when(drTracks.child(trackUID)).thenReturn(drTracksUID);
        when(drTracksUID.setValue(track)).thenReturn(setValueTrack);

    }

    private void instanciatedrKeys() {
        when(drKey.setValue(track)).thenReturn(setValueTrack);
        when(setValueTrack.addOnFailureListener(any(OnFailureListener.class))).thenAnswer(new Answer<Task<Void>>() {
            @Override
            public Task<Void> answer(InvocationOnMock invocation) throws Throwable {
                OnFailureListener l = (OnFailureListener) invocation.getArguments()[0];
                if (shouldFail) {
                    l.onFailure(new IllegalStateException("Could not add track to DB"));
                }
                return setValueTrack;
            }
        });
    }

    private void instanciateRead() {
        //Read tracks from drTracks
        doAnswer(new Answer<ValueEventListener>() {
            @Override
            public ValueEventListener answer(InvocationOnMock invocation) throws Throwable {
                ValueEventListener l = (ValueEventListener) invocation.getArguments()[0];
                if (isCancelled) {
                    l.onCancelled(snapOnDataErrorRead);
                } else {
                    l.onDataChange(snapOnDataChangeRead);
                }
                return l;
            }
        }).when(drTracks).addListenerForSingleValueEvent(any(ValueEventListener.class));


        //Read tracks from drKey
        doAnswer(new Answer<ValueEventListener>() {
            @Override
            public ValueEventListener answer(InvocationOnMock invocation) throws Throwable {
                ValueEventListener l = (ValueEventListener) invocation.getArguments()[0];
                if (isCancelled) {
                    l.onCancelled(snapOnDataErrorRead);
                } else {
                    l.onDataChange(snapOnDataChangeRead);
                }
                return l;
            }
        }).when(drKey).addListenerForSingleValueEvent(any(ValueEventListener.class));
    }

    private void createTrack() {
        Bitmap b = Util.createImage(200, 100, R.color.colorPrimary);
        Set<TrackType> types = new HashSet<>();
        types.add(TrackType.FOREST);
        CustLatLng coord0 = new CustLatLng(46.518577, 6.563165); //inm
        CustLatLng coord1 = new CustLatLng(46.522735, 6.579772); //Banane
        CustLatLng coord2 = new CustLatLng(46.519380, 6.580669); //centre sportif
        TrackProperties p = new TrackProperties(100, 10, 1, 1, types);
        Track track = new Track("0", "Bob", b, "Cours forest !", Arrays.asList(coord0, coord1, coord2), p);

        t = track;

    }
}
