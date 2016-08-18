package diuit.duolc.com.demoapplication;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.duolc.DiuitChat;
import com.duolc.DiuitChatType;
import com.duolc.DiuitMessage;
import com.duolc.DiuitMessagingAPISendingCallback;
import com.duolc.diuitapi.messageui.list.DiuitMessageListCell;
import com.duolc.diuitapi.messageui.message.DiuitMessageContentFactory;
import com.duolc.diuitapi.messageui.message.DiuitMessageView;
import com.duolc.diuitapi.messageui.message.DiuitSystemType;
import com.duolc.diuitapi.messageui.page.DiuitMessagesListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;

public class MessagesListActivity extends Activity {

    private DiuitMessagesListView messagesListView;
    private DiuitChat currentChat;

    private final int SELECT_FILE = 1001;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.initializeView();

        this.currentChat = Utils.selectedChat;

        /**
         * Step1. Setting DiuitMessagesListView
         **/
        this.messagesListView
            .setIgnoreSystemMessages(DiuitSystemType.KICK, DiuitSystemType.UPDATE_META, DiuitSystemType.UPDATE_WHITELIST)
            .setFetchCountForMessages(Utils.FETCHING_MESSAGES_COUNT_FOR_EACH)
            .bindChat(this.currentChat)
            .load();

        /**
         * Step2. Setting click event of settingBtn for showing Setting page
         **/
        this.messagesListView.getSettingBtn().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentChat.getType().equals(DiuitChatType.DIRECT)) {
                    MessagesListActivity.this.startActivity(new Intent(MessagesListActivity.this, DirectChatSettingActivity.class));
                } else {
                    MessagesListActivity.this.startActivity(new Intent(MessagesListActivity.this, GroupChatSettingActivity.class));
                }
             }
        });

        /**
         * Step3. Setting click event of Messages for showing Preview page
         **/
        this.messagesListView.getMessageListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(view instanceof DiuitMessageView) {
                    DiuitMessage message = ((DiuitMessageView) view).getMessage();
                    DiuitMessageContentFactory.Type type = DiuitMessageContentFactory.getType(message);

                    if(type.equals(DiuitMessageContentFactory.Type.IMAGE)
                        || type.equals(DiuitMessageContentFactory.Type.VIDEO)
                        || type.equals(DiuitMessageContentFactory.Type.FILE)
                        || type.equals(DiuitMessageContentFactory.Type.WEB)) {

                        Utils.selectedMessage = message;
                        MessagesListActivity.this.startActivity(new Intent(MessagesListActivity.this, PreviewActivity.class));
                    }
                }

            }
        });

        /**
         * Step4. Setting click event of AttachmentBtn
         **/
        this.messagesListView.getAddAttachmentBtn().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                startActivityForResult(Intent.createChooser(intent, "Select"), SELECT_FILE);
            }
        });
    }
    private void initializeView() {
        this.setContentView(R.layout.activity_messages_list);
        this.messagesListView = (DiuitMessagesListView) this.findViewById(R.id.messsagesListView);
    }

    /**
     * Step4-1. Getting selected photo
     **/
    @Override
    public void onActivityResult(int requestCode, final int resultCode, Intent data) {


        if (requestCode == SELECT_FILE) {

            if (resultCode == RESULT_OK) {

                final Uri uri = data.getData();
                final String type = this.getContentResolver().getType(uri);


                if(type.startsWith("image") ) { /*sending images*/

                    ContentResolver cr = this.getContentResolver();
                    try {
                        final Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                        JSONObject meta = new JSONObject();
                        try {
                            meta.put("name", "bitmap" );
                        }
                        catch (JSONException e) { e.printStackTrace(); }
                        currentChat.sendImage(bitmap, meta, new DiuitMessagingAPISendingCallback<DiuitMessage>()
                        {
                            @Override
                            public void onPreExecute(DiuitMessage diuitMessage) {
                                /**
                                 * Step5. Add DiuitMessage into DiuitMessagesListView
                                 **/
                                diuitMessage.setData(uri.toString());
                                messagesListView.getDiuitMessageListCells().add(new DiuitMessageListCell(diuitMessage));
                                messagesListView.getMessageListViewAdapter().notifyDataSetChanged();
                            }

                            @Override
                            public void onSuccess(final DiuitMessage diuitMessage) {
                                // successfully
                                if( bitmap != null)
                                    bitmap.recycle();
                                MessagesListActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        diuitMessage.setData(uri.toString());
                                        messagesListView.getMessageListViewAdapter().notifyDataSetChanged();
                                    }
                                });
                            }

                            @Override
                            public void onProgress(long loaded, long total) {
                                // TODO: 8/18/16
                            }

                            @Override
                            public void onFailure(final int code, final JSONObject result) {
                                // failure
                                if( bitmap != null)
                                    bitmap.recycle();
                                MessagesListActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        messagesListView.getMessageListViewAdapter().notifyDataSetChanged();
                                        Toast.makeText(MessagesListActivity.this, result.toString(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                    }
                    catch (FileNotFoundException e) { e.printStackTrace(); }

                } else { /*sending files*/

                    try {

                        final File file = new File( FileUtils.getPath(this, uri) );

                        JSONObject metaObj = new JSONObject();
                        metaObj.put("name", file.getName());
                        metaObj.put("size", file.length());

                        currentChat.sendFile(file, metaObj, new DiuitMessagingAPISendingCallback<DiuitMessage>() {
                            @Override
                            public void onPreExecute(DiuitMessage diuitMessage) {
                                /**
                                 * Step5. Add DiuitMessage into DiuitMessagesListView
                                 **/
                                diuitMessage.setData(uri.toString());
                                messagesListView.getDiuitMessageListCells().add(new DiuitMessageListCell(diuitMessage));
                                messagesListView.getMessageListViewAdapter().notifyDataSetChanged();
                            }

                            @Override
                            public void onSuccess(final DiuitMessage diuitMessage) {
                                // successfully
                                MessagesListActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        diuitMessage.setData(uri.toString());
                                        messagesListView.getMessageListViewAdapter().notifyDataSetChanged();
                                    }
                                });
                            }

                            @Override
                            public void onProgress(long loaded, long total) {
                                // TODO: 8/18/16
                            }

                            @Override
                            public void onFailure(final int code, final JSONObject result) {
                                MessagesListActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        messagesListView.getMessageListViewAdapter().notifyDataSetChanged();
                                        Toast.makeText(MessagesListActivity.this, result.toString(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                    }
                    catch (JSONException e) { e.printStackTrace(); }

                }




            }

        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
