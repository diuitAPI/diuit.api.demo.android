package diuit.duolc.com.demoapplication;

import android.app.Activity;
import android.os.Bundle;

import com.duolc.DiuitMessage;
import com.duolc.diuitapi.messageui.message.DiuitMessageContentFactory;
import com.duolc.diuitapi.messageui.preview.DiuitMessagePreviewFactory;

public class PreviewActivity extends Activity {

    private DiuitMessagePreviewFactory previewFactory;
    private DiuitMessage currentMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.initializeView();
        this.currentMessage = Utils.selectedMessage;

        DiuitMessageContentFactory.Type type = DiuitMessageContentFactory.getType(this.currentMessage);
        this.previewFactory.bindMessage(this.currentMessage).random();
    }

    private void initializeView() {
        this.setContentView(R.layout.activity_preview);
        this.previewFactory = (DiuitMessagePreviewFactory) this.findViewById(R.id.diuitPreview);
    }

}
