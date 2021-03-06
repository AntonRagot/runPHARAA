package ch.epfl.sweng.runpharaa.database.mock;

import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ch.epfl.sweng.runpharaa.database.firebase.TrackDatabaseManagement;
import ch.epfl.sweng.runpharaa.tracks.FirebaseTrackAdapter;
import ch.epfl.sweng.runpharaa.tracks.properties.TrackType;
import ch.epfl.sweng.runpharaa.user.User;
import ch.epfl.sweng.runpharaa.utils.Config;
import ch.epfl.sweng.runpharaa.utils.LatLngAdapter;

import static ch.epfl.sweng.runpharaa.user.User.serialize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

public class DatabaseMock {

    private final static String s_notification_key = "NotificationKey";
    private final static String COMMENTS = "comments";
    private final static String s_tracks = "tracks";
    private final static String s_user = "users";
    private final static String s_favorite = "favoriteTracks";
    private final static String s_likes = "likedTracks";
    private final static String s_create = "createdTracks";
    private final static String s_key = "key";
    private final static String s_feedback = "feedback";
    private final static String keyWriteTrack = "key";
    private final static User fake_user = new User("Bob", 2000, Uri.parse(""), new LatLng(21.23, 12.112), "1");
    private final static String fake_user_ser = serialize(fake_user);
    //Tracks already in the fakeDB
    private final static String trackUID = "TrackID";
    private final static String trackName = "name";
    private final static String s_following = "followedUsers";
    private static boolean shouldFail = false;
    private static boolean isCancelled = false;
    private static boolean userExists = false;
    private FirebaseTrackAdapter t = new FirebaseTrackAdapter();

    //For all mocked objects
    //First Level
    @Mock
    private FirebaseDatabase firebaseDatabaseMock;

    @Mock
    private DatabaseReference databaseReferenceMock;


    //Second level
    @Mock
    private DatabaseReference drTracks;

    @Mock
    private DatabaseReference drUser;


    //Third level
    @Mock
    private DatabaseReference drTracksPush;

    @Mock
    private DatabaseReference drKey;

    @Mock
    private DatabaseReference drTracksUID;

    @Mock
    private DatabaseReference drTracksUIDISDELETED;

    @Mock
    private DatabaseReference drTracksKey;

    @Mock
    private DatabaseReference drUserAnyChild;

    @Mock
    private DatabaseReference drUserAnyChildFavorites;

    @Mock
    private DatabaseReference drUserAnyChildLikes;

    @Mock
    private DatabaseReference drUserAnyChildCreate;

    @Mock
    private DatabaseReference drUserAnyChildName;

    @Mock
    private DatabaseReference drUserAnyChildPicture;

    @Mock
    private DatabaseReference drUserAnyChildFeedback;

    @Mock
    private DatabaseReference drUserAnyChildKey;

    @Mock
    private DataSnapshot snapInitUser;

    @Mock
    private DataSnapshot snapInitFollowedUsers;

    @Mock
    private DatabaseReference drUserAnyChildFavoritesChild;

    @Mock
    private DatabaseReference drUserAnyChildLikesChild;

    @Mock
    private DatabaseReference drUserAnyChildCreatesChild;

    @Mock
    private DatabaseReference drTracksUIDComment;

    @Mock
    private Task<Void> setValueTrack;

    @Mock
    private DataSnapshot snapOnDataChangeRead;

    @Mock
    private DataSnapshot snapOnDataChangeReadUser;

    @Mock
    private DataSnapshot snapInitFollow;

    @Mock
    private DataSnapshot snapInitCurUser;

    @Mock
    private DataSnapshot snapInitCurUserName;

    @Mock
    private DataSnapshot snapInitChildrenUser;

    @Mock
    private DataSnapshot snapInitFollowChildrens;

    @Mock
    private DataSnapshot snapOnDataChangeReadChildPath;

    @Mock
    private DataSnapshot snapOnDataChangedChildTrackUID;

    @Mock
    private DataSnapshot snapOnDataChangeReadChildPath0;

    @Mock
    private DatabaseError snapOnDataErrorRead;

    @Mock
    private DatabaseException dbExceptionReadTrack;

    @Mock
    private DataSnapshot snapInitChildrenID;

    @Mock
    private DataSnapshot snapOnDataUserKey;

    @Mock
    private DataSnapshot snapOnDataFollowSystem;

    @Mock
    private DataSnapshot snapOnDataChangeUser;

    @Mock
    private DataSnapshot snapOnDataFollowSystemUser;

    @Mock
    private DataSnapshot snapOnDataChangeUserChild;

    @Mock
    private DataSnapshot snapOnDataChangeReadChildIsDeleted;

    @Mock
    private DatabaseError snapOnDataErrorUser;

    @Mock
    private DataSnapshot snapInit;

    @Mock
    private DataSnapshot snapInitTrack;

    @Mock
    private DataSnapshot snapOnDataUserName;

    @Mock
    private DataSnapshot snapOnDataUserPicture;

    @Mock
    private DataSnapshot snapInitChildren;

    @Mock
    private FirebaseTrackAdapter track;

    @Mock
    private List<String> userFavoritesList;

    @Mock
    private List<String> userFollowedList;

    @Mock
    private List<String> userLikesList;

    @Mock
    private List<String> userCreatesList;

    @Mock
    private Task<Void> removeTask;

    @Mock
    private Task<Void> setTask;

    @Mock
    private Task<Void> addComment;

    @Mock
    private Task<Void> setValueTask;

    @Mock
    private Task<Void> setValueFavoriteTask;

    @Mock
    private Task<Void> setValueLikeTask;

    @Mock
    private Task<Void> userTask;

    @Mock
    private DatabaseReference drUserAnyChildFollow;


    private Answer snapOnDataChangedAnswer = invocation -> {
        ValueEventListener l = (ValueEventListener) invocation.getArguments()[0];
        if (isCancelled) {
            l.onCancelled(snapOnDataErrorRead);
        } else {
            l.onDataChange(snapOnDataChangeReadUser);
        }
        return null;
    };

    private DatabaseMock() {

    }

    public static FirebaseDatabase getInstance() {
        return (Config.isTest) ? new DatabaseMock().instantiateMock() : FirebaseDatabase.getInstance();
    }

    public static void setShouldFail(boolean shouldFail) {
        DatabaseMock.shouldFail = shouldFail;
    }

    public static void setIsCancelled(boolean isCancelled) {
        DatabaseMock.isCancelled = isCancelled;
    }

    public static void setUserExists(boolean userExists) {
        DatabaseMock.userExists = userExists;
    }

    public static User getUser() {
        return fake_user;
    }

    private FirebaseDatabase instantiateMock() {
        MockitoAnnotations.initMocks(this);
        createTrack();
        instantiateDB();
        instantiateDBRef();
        instantiateDrTracks();
        instanciateDrKeys();
        instantiatedrUsers();
        instantiateRead();
        instantiateSnapshots();
        return firebaseDatabaseMock;
    }

    private void instantiateDB() {
        when(firebaseDatabaseMock.getReference()).thenReturn(databaseReferenceMock);
    }

    private void instantiateDBRef() {
        when(databaseReferenceMock.child(s_tracks)).thenReturn(drTracks);
        when(databaseReferenceMock.child(s_key)).thenReturn(drKey);
        when(databaseReferenceMock.child(s_user)).thenReturn(drUser);
    }

    private void instantiateSnapshots() {
        when(snapInit.child(s_tracks)).thenReturn(snapInitTrack);

        when(snapOnDataChangeReadUser.getChildren()).thenReturn(Collections.singletonList(snapInitChildrenUser));
        when(snapOnDataChangeRead.getChildren()).thenReturn(Collections.singletonList(snapInitChildren));
        when(snapOnDataChangeRead.child(trackUID)).thenReturn(snapInitChildren);
        when(snapOnDataChangeRead.child("0")).thenReturn(snapInitChildren);

        when(snapOnDataChangeRead.child("1")).thenReturn(snapInitFollow);
        when(snapInitFollow.getChildren()).thenReturn(Collections.singletonList(snapInitFollowChildrens));
        when(snapInitFollowChildrens.getValue()).thenReturn(null);

        //changer le nom
        when(snapOnDataChangeReadUser.child(any(String.class))).thenReturn(snapInitCurUser);
        //when(snapOnDataChangeReadUser.child("1")).thenReturn(snapInitCurUser);
        when(snapInitCurUser.child("followedUsers")).thenReturn(snapInitUser);
        when(snapInitChildrenUser.child("name")).thenReturn(snapInitUser);
        when(snapInitUser.getChildren()).thenReturn(Collections.singletonList(snapInitFollowedUsers));
        when(snapInitFollowedUsers.getValue()).thenReturn(fake_user_ser);
        when(snapInitUser.getValue((String.class))).thenReturn("Bob");
        when(snapInitUser.exists()).thenReturn(true);
        when(snapInitCurUser.exists()).thenReturn(true);
        when(snapInitCurUser.child("name")).thenReturn(snapInitCurUserName);
        when(snapInitCurUserName.getValue()).thenReturn("Bob");

        when(snapInitChildrenUser.getValue()).thenReturn(fake_user);
        when(snapInitChildrenUser.getKey()).thenReturn("1");
        when(snapInitChildrenUser.child("uid")).thenReturn(snapInitChildrenID);
        when(snapInitChildrenID.getValue((String.class))).thenReturn("1");

        when(snapOnDataUserKey.exists()).thenReturn(Boolean.TRUE);
        when(snapOnDataUserKey.getValue((String.class))).thenReturn("NOTIFICATIONKEY");


        when(snapInitChildren.child(trackName)).thenReturn(snapOnDataChangeReadChildPath);
        when(snapInitChildren.getValue(FirebaseTrackAdapter.class)).thenReturn(t);
        when(snapInitChildren.child("path")).thenReturn(snapOnDataChangeReadChildPath);
        when(snapInitChildren.child("trackUid")).thenReturn(snapOnDataChangedChildTrackUID);
        when(snapInitChildren.getKey()).thenReturn("0");
        when(snapInitChildren.child("isDeleted")).thenReturn(snapOnDataChangeReadChildIsDeleted);

        when(snapOnDataChangeReadChildPath.getValue((String.class))).thenReturn("Cours forest !");
        when(snapOnDataChangedChildTrackUID.getValue((String.class))).thenReturn(trackUID);
        when(snapOnDataChangeReadChildPath.child("0")).thenReturn(snapOnDataChangeReadChildPath0);
        when(snapOnDataChangeReadChildPath0.getValue(LatLngAdapter.class)).thenReturn(new LatLngAdapter(37.422, -122.084));
        when(snapOnDataChangeReadChildIsDeleted.getValue(Boolean.class)).thenReturn(false);

        when(snapOnDataChangeUser.exists()).thenReturn(true);
        when(snapOnDataChangeUser.child(any(String.class))).thenReturn(snapOnDataChangeUserChild);
        when(snapOnDataChangeUserChild.exists()).thenReturn(false);
        when(snapOnDataChangeUser.getValue(User.class)).thenReturn(new User("USER_WRITTEN", 2000, Uri.parse(""), new LatLng(21.23, 12.112), "42"));

        when(snapOnDataUserName.exists()).thenReturn(true);
        when(snapOnDataUserName.getValue((String.class))).thenReturn("Bob");

        when(snapOnDataUserPicture.exists()).thenReturn(true);
        when(snapOnDataUserPicture.getValue((String.class))).thenReturn("");

        when(snapOnDataFollowSystem.child(any(String.class))).thenReturn(snapOnDataFollowSystemUser);
        when(snapOnDataFollowSystemUser.child(s_following)).thenReturn(snapInitFollowChildrens);


    }

    private void instantiatedrUsers() {
        when(drUser.child(any(String.class))).thenReturn(drUserAnyChild);

        doAnswer((Answer<ValueEventListener>) invocation -> {
            ValueEventListener l = (ValueEventListener) invocation.getArguments()[0];
            if (isCancelled) {
                l.onCancelled(snapOnDataErrorUser);
            } else {
                l.onDataChange(snapOnDataFollowSystem);
            }
            return l;
        }).when(drUser).addListenerForSingleValueEvent(any(ValueEventListener.class));

        //write new user
        doAnswer((Answer<ValueEventListener>) invocation -> {
            ValueEventListener l = (ValueEventListener) invocation.getArguments()[0];
            if (isCancelled) {
                l.onCancelled(snapOnDataErrorUser);
            } else {
                l.onDataChange(snapOnDataChangeUser);
            }
            return l;
        }).when(drUserAnyChild).addListenerForSingleValueEvent(any(ValueEventListener.class));

        //---

        when(drUserAnyChild.setValue(any(User.class))).thenReturn(userTask);

        when(userTask.addOnFailureListener(any(OnFailureListener.class))).thenAnswer((Answer<Task<Void>>) invocation -> {
            OnFailureListener l = (OnFailureListener) invocation.getArguments()[0];
            if (shouldFail) {
                l.onFailure(new IllegalStateException());
            }
            return userTask;
        });

        respondToAddSuccessListener(userTask);

        //----

        when(drUserAnyChild.child(s_favorite)).thenReturn(drUserAnyChildFavorites);
        when(drUserAnyChild.child(s_likes)).thenReturn(drUserAnyChildLikes);
        when(drUserAnyChild.child(s_create)).thenReturn(drUserAnyChildCreate);
        when(drUserAnyChild.child(s_feedback)).thenReturn(drUserAnyChildFeedback);
        when(drUserAnyChild.child("name")).thenReturn(drUserAnyChildName);
        when(drUserAnyChild.child("picture")).thenReturn(drUserAnyChildPicture);
        when(drUserAnyChild.child(s_notification_key)).thenReturn(drUserAnyChildKey);


        when(drUserAnyChild.child("followedUsers")).thenReturn(drUserAnyChildFollow);

        when(drUserAnyChildFollow.setValue(userFollowedList)).thenAnswer(new Answer<Task<Void>>() {
            @Override
            public Task<Void> answer(InvocationOnMock invocation) {
                fake_user.setFollowedUsers(userFollowedList);
                return null;
            }
        });

        when(drUserAnyChildFavorites.setValue(userFavoritesList)).thenAnswer(new Answer<Task<Void>>() {
            @Override
            public Task<Void> answer(InvocationOnMock invocation) {
                fake_user.setFavoriteTracks(userFavoritesList);
                return null;
            }
        });

        when(drUserAnyChildLikes.setValue(userLikesList)).thenAnswer(new Answer<Task<Void>>() {
            @Override
            public Task<Void> answer(InvocationOnMock invocation) {
                fake_user.setLikedTracks(userLikesList);
                return null;
            }
        });

        when(drUserAnyChildLikes.setValue(userCreatesList)).thenAnswer(new Answer<Task<Void>>() {
            @Override
            public Task<Void> answer(InvocationOnMock invocation) {
                fake_user.setCreatedTracks(userCreatesList);
                return null;
            }
        });

        instantiateSetTrackListToUser();

        when(drUserAnyChildFavorites.child(any(String.class))).thenReturn(drUserAnyChildFavoritesChild);
        when(drUserAnyChildLikes.child(any(String.class))).thenReturn(drUserAnyChildLikesChild);
        when(drUserAnyChildCreate.child(any(String.class))).thenReturn(drUserAnyChildCreatesChild);


        when(drUserAnyChildCreatesChild.setValue(any(String.class))).thenReturn(setValueTask);

        when(drUserAnyChildFavorites.setValue(any(Object.class))).thenReturn(setValueFavoriteTask);
        when(drUserAnyChildLikes.setValue(any(Object.class))).thenReturn(setValueLikeTask);
        when(drUserAnyChildFollow.setValue(any(Object.class))).thenReturn(setValueTask);

        instantiateListenersForSingleValueEvent();

        when(drUserAnyChildFavoritesChild.removeValue()).thenAnswer((Answer<Task<Void>>) invocation -> removeTask);
        when(drUserAnyChildLikesChild.removeValue()).thenAnswer((Answer<Task<Void>>) invocation -> removeTask);

        when(drUserAnyChildLikes.setValue(any(List.class))).thenReturn(setValueTask);
        when(drUserAnyChildFavorites.setValue(any(List.class))).thenReturn(setValueTask);
        when(drUserAnyChildCreate.setValue(any(List.class))).thenReturn(setValueTask);
        when(drUserAnyChildFeedback.setValue(any(List.class))).thenReturn(setValueTask);

        when(drUserAnyChildCreatesChild.setValue(any(String.class))).thenReturn(setValueTask);
        when(drUserAnyChildLikesChild.setValue(any(String.class))).thenReturn(setValueTask);
        when(drUserAnyChildFavoritesChild.setValue(any(String.class))).thenReturn(setValueTask);

        instantiateUserOnFailureListeners();
    }

    private void instantiateUserOnFailureListeners() {
        when(setValueTask.addOnFailureListener(any(OnFailureListener.class))).thenAnswer((Answer<Task<Void>>) invocation -> {
            OnFailureListener l = (OnFailureListener) invocation.getArguments()[0];
            if (shouldFail) {
                l.onFailure(new IllegalStateException("Cant set value"));
            }
            return setValueTrack;
        });
    }

    private void instantiateListenersForSingleValueEvent() {
        //instantiateUserOperationsOnSingleTrack();

        doAnswer((Answer<ValueEventListener>) invocation -> {
            ValueEventListener l = (ValueEventListener) invocation.getArguments()[0];
            if (isCancelled) {
                l.onCancelled(snapOnDataErrorRead);
            } else {
                l.onDataChange(snapOnDataUserPicture);
            }
            return l;
        }).when(drUserAnyChildPicture).addListenerForSingleValueEvent(any(ValueEventListener.class));

        doAnswer((Answer<ValueEventListener>) invocation -> {
            ValueEventListener l = (ValueEventListener) invocation.getArguments()[0];
            if (isCancelled) {
                l.onCancelled(snapOnDataErrorRead);
            } else {
                l.onDataChange(snapOnDataUserName);
            }
            return l;
        }).when(drUserAnyChildName).addListenerForSingleValueEvent(any(ValueEventListener.class));

        doAnswer((Answer<ValueEventListener>) invocation -> {
            ValueEventListener l = (ValueEventListener) invocation.getArguments()[0];
            if (isCancelled) {
                l.onCancelled(snapOnDataErrorRead);
            } else {
                l.onDataChange(snapOnDataUserKey);
            }
            return l;
        }).when(drUserAnyChildKey).addListenerForSingleValueEvent(any(ValueEventListener.class));
    }

    private void instantiateSetTrackListToUser() {
        when(drUserAnyChildFavorites.setValue(userFavoritesList)).thenAnswer((Answer<Task<Void>>) invocation -> {
            fake_user.setFavoriteTracks(userFavoritesList);
            return null;
        });

        when(drUserAnyChildLikes.setValue(userLikesList)).thenAnswer((Answer<Task<Void>>) invocation -> {
            fake_user.setLikedTracks(userLikesList);
            return null;
        });

        when(drUserAnyChildLikes.setValue(userCreatesList)).thenAnswer((Answer<Task<Void>>) invocation -> {
            fake_user.setCreatedTracks(userCreatesList);
            return null;
        });
    }

    private void instantiateDrTracks() {
        when(drTracks.push()).thenReturn(drTracksPush);
        when(drTracksPush.getKey()).thenReturn(keyWriteTrack);

        when(drTracks.child(trackUID)).thenReturn(drTracksUID);
        when(drTracksUID.setValue(track)).then((Answer<Task<Void>>) invocation -> {
            t = track;
            return null;
        });


        when(drTracksUID.child(COMMENTS)).thenReturn(drTracksUIDComment);
        when(drTracksUIDComment.setValue(any(List.class))).thenReturn(addComment);

        respondToAddFailureListener(addComment);

        when(drTracksUID.child(TrackDatabaseManagement.IS_DELETED)).thenReturn(drTracksUIDISDELETED);
        doAnswer((Answer<ValueEventListener>) invocation -> {
            ValueEventListener l = (ValueEventListener) invocation.getArguments()[0];
            if (isCancelled) {
                l.onCancelled(snapOnDataErrorRead);
            } else {
                l.onDataChange(snapOnDataChangeRead);
            }
            return l;
        }).when(drTracksUIDISDELETED).addListenerForSingleValueEvent(any(ValueEventListener.class));

        when(drTracks.child(keyWriteTrack)).thenReturn(drTracksKey);
        when(drTracksKey.setValue(any(FirebaseTrackAdapter.class))).thenReturn(setTask);

        respondToAddFailureListener(setTask);

        respondToAddSuccessListener(setTask);

    }

    private void respondToAddFailureListener(Task task) {
        when(task.addOnFailureListener(any(OnFailureListener.class))).thenAnswer((Answer<Task<Void>>) invocation -> {
            OnFailureListener l = (OnFailureListener) invocation.getArguments()[0];
            if (shouldFail) {
                l.onFailure(new IllegalStateException());
            }
            return task;
        });
    }

    private void respondToAddSuccessListener(Task task) {
        when(task.addOnSuccessListener(any(OnSuccessListener.class))).thenAnswer((Answer<Task<Void>>) invocation -> {
            OnSuccessListener<Void> l = (OnSuccessListener<Void>) invocation.getArguments()[0];
            if (!shouldFail) {
                l.onSuccess(null);
            }
            return task;
        });
    }

    private void instanciateDrKeys() {
        when(drKey.setValue(track)).thenReturn(setValueTrack);
    }

    private void instantiateRead() {
        //Read tracks from drTracks
        doAnswer((Answer<ValueEventListener>) invocation -> {
            ValueEventListener l = (ValueEventListener) invocation.getArguments()[0];
            if (isCancelled) {
                l.onCancelled(snapOnDataErrorRead);
            } else {
                l.onDataChange(snapOnDataChangeRead);
            }
            return l;
        }).when(drTracks).addListenerForSingleValueEvent(any(ValueEventListener.class));

        doAnswer(snapOnDataChangedAnswer).when(drUser).addListenerForSingleValueEvent(any(ValueEventListener.class));


        doAnswer(snapOnDataChangedAnswer).when(drUserAnyChildFollow).addListenerForSingleValueEvent(any(ValueEventListener.class));

    }

    private void instantiateError() {
        when(snapOnDataErrorRead.toException()).thenReturn(dbExceptionReadTrack);
    }

    private void createTrack() {
        List<String> types = new ArrayList<>();
        types.add(TrackType.FOREST.toString());
        LatLngAdapter coord0 = new LatLngAdapter(37.422, -122.084);
        LatLngAdapter coord1 = new LatLngAdapter(37.425, -122.082);
        int length = 100;
        int heightDiff = 10;

        t = new FirebaseTrackAdapter("Cours forest !", trackUID, "1",
                "Bob", Arrays.asList(coord0, coord1), "imageUri",
                types, length, heightDiff, 1, 1, 1, 1,
                0, 0, new ArrayList<>());

    }
}
