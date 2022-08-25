package com.team7.app_chat.ui.chat;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.team7.app_chat.CurrentUser;
import com.team7.app_chat.MainActivity;
import com.team7.app_chat.R;
import com.team7.app_chat.Util.RoomChatRepository;
import com.team7.app_chat.Util.UserRepository;
import com.team7.app_chat.adapters.MessageAdapter;
import com.team7.app_chat.adapters.UsersAdapter;
import com.team7.app_chat.models.Contact;
import com.team7.app_chat.models.Member;
import com.team7.app_chat.models.Message;
import com.team7.app_chat.models.RoomChat;
import com.team7.app_chat.models.User;
import com.team7.app_chat.ui.contacts.ContactsFragment;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class ChatFragment extends Fragment {
    private RoomChatRepository repository;
    private DocumentReference roomRef;
    private RoomChat chatRoom;
    private User currentUser;
    private String roomId;
    private String userId;
    private int member;

    private View roomView;
    private RecyclerView recyclerView;
    private ImageView avatarView;

    private ActivityResultLauncher<String[]> permissionContract;
    private ActivityResultLauncher<Uri> takePhotoContract;
    private ActivityResultLauncher<String> pickImageContract;

    private Uri sourceUri;
    private StorageReference storageRef;
    private boolean isCapture;

    private DocumentReference currentUserRef;
    private DocumentReference friendRef;
    private UserRepository userRepository;

    private Button bottomsheet;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        createContract();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentUser = CurrentUser.user;
        repository = new RoomChatRepository();
        userRepository = new UserRepository();
        storageRef = FirebaseStorage.getInstance().getReference();
        roomId = getArguments().getString("id");
        userId = getArguments().getString("userId");
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        roomView = inflater.inflate(R.layout.fragment_chat,
                container, false);
        bottomsheet = roomView.findViewById(R.id.btnAttachment);
        recyclerView = roomView.findViewById(R.id.listMessage);
        avatarView = roomView.findViewById(R.id.chatAvatar);
        bottomsheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showDialog();

            }
        });
        roomView.findViewById(R.id.btnBackChat).setOnClickListener(view -> {
            NavHostFragment.findNavController(ChatFragment.this).navigate(R.id.action_chat_room_to_chat);
        });
        ((EditText) roomView.findViewById(R.id.edtMessage)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    roomView.findViewById(R.id.btnTakePic).setVisibility(View.GONE);
                    roomView.findViewById(R.id.btnChoosePic).setVisibility(View.GONE);
//                    roomView.findViewById(R.id.btnEmoji).setVisibility(View.GONE);
                    roomView.findViewById(R.id.btnSend).setVisibility(View.VISIBLE);
                } else {
                    roomView.findViewById(R.id.btnTakePic).setVisibility(View.VISIBLE);
                    roomView.findViewById(R.id.btnChoosePic).setVisibility(View.VISIBLE);
//                    roomView.findViewById(R.id.btnEmoji).setVisibility(View.VISIBLE);
                    roomView.findViewById(R.id.btnSend).setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        roomView.findViewById(R.id.btnSend).setOnClickListener(view -> sendMessage());
        roomView.findViewById(R.id.btnChoosePic).setOnClickListener(view -> {
            pickImageContract();
        });
        roomView.findViewById(R.id.btnTakePic).setOnClickListener(view -> {
            captureImageContract();
        });
//        ConstraintLayout rootLayout = roomView.findViewById(R.id.layoutChat);
//        EditText edMessage = roomView.findViewById(R.id.edtMessage);
//        EmojiPopup popup = new EmojiPopup(rootLayout, edMessage);
        roomView.findViewById(R.id.btnEmoji).setOnClickListener(view -> {
//            popup.toggle();
        });
//        roomView.findViewById(R.id.btInfo).setOnClickListener(view -> {
//            Bundle bundle = new Bundle();
//            bundle.putString("roomId", roomId);
//            NavHostFragment.findNavController(this).navigate(R.id.action_chatFragment_to_roomSettingFragment, bundle);
//        });

        return roomView;
    }


    private void showDialog() {

        final Dialog dialog = new Dialog(roomView.getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_sheet);

        LinearLayout editLayout = dialog.findViewById(R.id.layoutEdit);
        LinearLayout shareLayout = dialog.findViewById(R.id.layoutShare);
        LinearLayout uploadLayout = dialog.findViewById(R.id.layoutUpload);
        LinearLayout printLayout = dialog.findViewById(R.id.layoutPrint);

        editLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                Toast.makeText(roomView.getContext(), "Edit is Clicked", Toast.LENGTH_SHORT).show();

            }
        });

        shareLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                Toast.makeText(roomView.getContext(), "Share is Clicked", Toast.LENGTH_SHORT).show();

            }
        });

        uploadLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                Toast.makeText(roomView.getContext(), "Upload is Clicked", Toast.LENGTH_SHORT).show();

            }
        });

        printLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                Toast.makeText(roomView.getContext(), "Print is Clicked", Toast.LENGTH_SHORT).show();

            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        currentUserRef = userRepository.getDocRf(currentUser.getId());
        if (roomId != null) {
            roomRef = repository.get().document(roomId);
            roomRef.addSnapshotListener((value, error) -> {
                if (error != null) return;
                ((DocumentReference) value.get("lastMessage")).update("viewer", FieldValue.arrayUnion(currentUserRef));
            });
            checkMember();
        } else if (userId != null) {
            userRepository.getDocRf(userId).get().addOnSuccessListener(documentSnapshot -> {
                friendRef = documentSnapshot.getReference();
                User user = documentSnapshot.toObject(User.class);
                Glide.with(this).load(user.getAvatar()).into(avatarView);
                ((TextView) roomView.findViewById(R.id.chatName)).setText(user.getFullName());
            });
        }
    }

    private void createContract() {

        takePhotoContract = registerForActivityResult(new ActivityResultContracts.TakePicture(), status -> {
            if (status) {
                uploadImages();
            }
        });

        permissionContract = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                AtomicBoolean status = new AtomicBoolean(true);
                result.forEach((key, val) -> {
                    if (!val) {
                        status.set(false);
                    }
                });
                if (!status.get()) {
                    Toast.makeText(getActivity(), "Please allow permission to use this feature!", Toast.LENGTH_SHORT).show();
                } else {
                    if (isCapture) {
                        if (checkCameraPermission()) {
                            sourceUri = createImageUri();
                            takePhotoContract.launch(sourceUri);
                        }
                    } else {
                        if (checkStoragePermission()) {
                            pickImageContract.launch("image/*");
                        }
                    }
                }
            }
        });

        pickImageContract = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            sourceUri = uri;
            uploadImages();
        });
    }

    private boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        boolean result1 = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        return result && result1;
    }

    private boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        boolean result1 = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        return result && result1;
    }

    private void pickImageContract() {
        String permission[] = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        isCapture = false;
        permissionContract.launch(permission);

    }

    private void captureImageContract() {
        String permission[] = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        isCapture = true;
        permissionContract.launch(permission);

    }

    private Uri createImageUri() {
        Uri imageCollection;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            imageCollection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
        } else {
            imageCollection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        }
        String imageName = String.valueOf(System.currentTimeMillis());

        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, imageName);
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        Uri finalUri = getActivity().getContentResolver().insert(imageCollection, contentValues);
        return finalUri;
    }

    private void uploadImages() {
        Toast.makeText(getActivity(), "Uploading...", Toast.LENGTH_SHORT).show();
        String fileName = String.valueOf(System.currentTimeMillis());
        StorageReference imgRef = storageRef.child("images").child(fileName);
        imgRef.putFile(sourceUri).addOnSuccessListener(taskSnapshot -> {
            imgRef.getDownloadUrl().addOnSuccessListener(uri -> {
                Message message = new Message();
                ArrayList<DocumentReference> list = new ArrayList<>();
                list.add(currentUserRef);
                message.setViewer(list);
                message.setText(currentUser.getFullName() + " sent a image");
                message.setFile(true);
                message.setFileUrl(uri.toString());
                message.setCreatedDate(new Date());
                message.setSendBy(currentUserRef);
                roomRef.collection("messages").add(message).addOnSuccessListener(documentReference -> {
                    ((TextView) roomView.findViewById(R.id.edtMessage)).setText("");
                    roomRef.update("lastMessage", documentReference);
                    roomRef.update("updateAt", new Date());
                    Toast.makeText(getActivity(), "Completed!", Toast.LENGTH_SHORT).show();
                });
            });
        });
    }

    private void checkMember() {
        roomRef.getId();
        roomRef.collection("members").addSnapshotListener((value, error) -> {
            if (error != null) return;
            member = value.size();
            int count = (int) value.getDocuments().stream().filter(val -> val.getId().equals(currentUser.getId())).count();
            if (getActivity() != null) {
                if (count == 0) {
                    NavHostFragment.findNavController(this).popBackStack();
                    Toast.makeText(getActivity(), "You has been banned from the chat room", Toast.LENGTH_SHORT).show();
                } else {
                    roomRef.get().addOnSuccessListener(documentSnapshot -> {
                        chatRoom = documentSnapshot.toObject(RoomChat.class);
                        if (chatRoom.getName() == null) {
                            getFriendInfo(value.getDocuments());
                        } else {
                            Glide.with(getActivity()).load(chatRoom.getAvatar()).into(avatarView);
                            ((TextView) roomView.findViewById(R.id.chatName)).setText(chatRoom.getName());
                        }
                    });
                    loadMessage();
                }
            }
        });
    }

    private void getFriendInfo(List<DocumentSnapshot> documents) {
        DocumentSnapshot member = documents.stream()
                .filter(doc -> !doc.toObject(Member.class).getUser().getId().equals(currentUser.getId()))
                .findFirst().get();
        member.toObject(Member.class).getUser().addSnapshotListener((value, error) -> {
            if (error != null) return;
            User user = value.toObject(User.class);
            Glide.with(this).load(user.getAvatar()).into(avatarView);
            String fullName = user.getFullName();
            ((TextView) roomView.findViewById(R.id.chatName)).setText(fullName);
        });
    }

    private void loadMessage() {
        List<DocumentSnapshot> list = new ArrayList<>();
        MessageAdapter adapter = new MessageAdapter(list, getContext(), member);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.setStackFromEnd(true);

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        roomRef.collection("messages")
                .orderBy("createdDate")
                .addSnapshotListener((value, error) -> {
                    for (DocumentChange dc : value.getDocumentChanges()) {
                        switch ((dc.getType())) {
                            case ADDED:
                                list.add(dc.getDocument());

                                adapter.notifyDataSetChanged();
                                recyclerView.scrollToPosition(list.size() - 1);

                                break;
                        }
                    }

                });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void sendMessage() {
        Message message = new Message();
        message.setCreatedDate(new Date());
        String text = ((TextView) roomView.findViewById(R.id.edtMessage)).getText().toString();
        message.setText(text);
        message.setSendBy(currentUserRef);

        try {
            ArrayList<DocumentReference> viewers = new ArrayList<>();
            viewers.add(currentUserRef);
            message.setViewer(viewers);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (roomId != null) {
            ((TextView) roomView.findViewById(R.id.edtMessage)).setText("");
            repository.createMessage(roomId, message);

        } else {
            RoomChat chatRoom = new RoomChat();
            chatRoom.setPublic(false);

            Member current = new Member();
            current.setMod(true);
            current.setUser(currentUserRef);

            Member friend = new Member();
            friend.setMod(true);
            friend.setUser(friendRef);

            repository.get().add(chatRoom).addOnSuccessListener(documentReference -> {
                ((TextView) roomView.findViewById(R.id.edtMessage)).setText("");
                roomRef = documentReference;
                documentReference.collection("members").document(currentUserRef.getId()).set(current);
                documentReference.collection("members").document(friendRef.getId()).set(friend);
                documentReference.collection("messages").add(message).addOnSuccessListener(doc -> {
                    documentReference.update("lastMessage", doc);
                });
                Map<String, Object> room = new HashMap<>();
                room.put("room", documentReference);
                room.put("updatedAt", new Date());
                currentUserRef.collection("chatRoom").document(roomRef.getId()).set(room);
                friendRef.collection("chatRoom").document(roomRef.getId()).set(room);
                checkMember();
            });
        }
    }

}
