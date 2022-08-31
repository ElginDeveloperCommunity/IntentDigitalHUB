package com.elgin.java_intentdigitalhub_smartpos.Activities.Printer.PrinterPages;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import com.elgin.java_intentdigitalhub_smartpos.Activities.ActivityUtils;
import com.elgin.java_intentdigitalhub_smartpos.Activities.Printer.PrinterMenuActivity;
import com.elgin.java_intentdigitalhub_smartpos.IntentDigitalHubService.IntentDigitalHubCommand;
import com.elgin.java_intentdigitalhub_smartpos.IntentDigitalHubService.IntentDigitalHubCommandStarter;
import com.elgin.java_intentdigitalhub_smartpos.IntentDigitalHubService.TERMICA.Commands.AvancaPapel;
import com.elgin.java_intentdigitalhub_smartpos.IntentDigitalHubService.TERMICA.Commands.Corte;
import com.elgin.java_intentdigitalhub_smartpos.IntentDigitalHubService.TERMICA.Commands.ImprimeImagem;
import com.elgin.java_intentdigitalhub_smartpos.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Para esta funcionalidade, como não há como enviar uma imagem via JSON, é feito o salvamento de uma imagem dentro do diretório externo da aplicação (Em /Android/data/..) e este path é enviado ao Intent Digital Hub como parâmetro.
 */
public class PrinterImageActivity extends AppCompatActivity {

    //Imagem de pré-visualização.
    private ImageView imageView;

    //Botões de seleção de imagem e de impressão.
    private Button buttonSelectImage, buttonPrintImage;

    //Checkbox de corte de papel, só será disponibilizada caso o método de impressão escolhido seja por impressora externa.
    private CheckBox checkBoxIsCutPaperImage;

    //PATH da imagem a ser impressa.
    private String pathOfLastSelectedImage;

    //Int usado para inicio da atividade;
    private final int OPEN_GALLERY_FOR_IMAGE_SELECTION_REQUESTCODE = 1;
    private final int IMPRIME_IMAGEM_REQUESTCODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_printer_image);

        //Atribui path onde a imagem será salva. (Android/data/pacoteDaAplicacao/Files/ImageToPrint.jpg
        this.pathOfLastSelectedImage = "/Android/data/" + this.getApplicationContext().getPackageName() + "/Files" + "/ImageToPrint.jpg";

        //Atribui as views ao iniciar da tela.
        viewsAssignment();

        //Estado inicial da tela.
        initialState();

        //Atribui as funcionalidades de cada view.
        viewsFunctionalityAssignment();
    }

    //Atribuição das views.
    private void viewsAssignment() {
        imageView = findViewById(R.id.previewImgDefault);

        buttonSelectImage = findViewById(R.id.buttonSelectImage);
        buttonPrintImage = findViewById(R.id.buttonPrintImage);

        checkBoxIsCutPaperImage = findViewById(R.id.checkBoxIsCutPaperImage);
    }

    //Aplica algumas configurações iniciais de tela.
    private void initialState() {
        //O corte de papel só esta disponível em impressões por impressora externa, caso a opção escolhida no menu de impressora não tenha sido impressora externa, a checkbox de corte de papel deve sumir.
        if (PrinterMenuActivity.selectedPrinterConnectionType != PrinterMenuActivity.PrinterConnectionMethod.EXTERN)
            checkBoxIsCutPaperImage.setVisibility(View.INVISIBLE);

        //Para que ao abrir a tela, exista uma imagem pré salva no diretório da aplicação para que a impressão seja feita via PATH, é feito o salvamento da imagem padrão na abertura da tela.
        Bitmap elgin_logo_default_print_image = BitmapFactory.decodeResource(this.getResources(), R.drawable.elgin_logo_default_print_image);

        storeImage(elgin_logo_default_print_image);
    }

    //Atribuição das funcionalidades das views.
    private void viewsFunctionalityAssignment() {
        buttonSelectImage.setOnClickListener(v -> startGallery());
        buttonPrintImage.setOnClickListener(v -> printImage());
    }

    //Abre a galeria para a selação de uma imagem para impressão.
    private void startGallery() {
        Toast.makeText(this, "Selecione uma imagem com no máximo 400 pixels de largura!", Toast.LENGTH_LONG).show();
        Intent cameraIntent = new Intent(Intent.ACTION_PICK);

        cameraIntent.setType("image/*");

        startActivityForResult(cameraIntent, OPEN_GALLERY_FOR_IMAGE_SELECTION_REQUESTCODE);
    }

    //Realiza a impressão da imagem por PATH.
    private void printImage() {
        List<IntentDigitalHubCommand> termicaCommands = new ArrayList<>();

        ImprimeImagem imprimeImagemCommand = new ImprimeImagem(pathOfLastSelectedImage);

        termicaCommands.add(imprimeImagemCommand);

        AvancaPapel avancaPapelCommand = new AvancaPapel(10);

        termicaCommands.add(avancaPapelCommand);

        if (checkBoxIsCutPaperImage.isChecked()) {
            Corte corteCommand = new Corte(0);

            termicaCommands.add(corteCommand);
        }

        IntentDigitalHubCommandStarter.startIDHCommandForResult(this, termicaCommands, IMPRIME_IMAGEM_REQUESTCODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == OPEN_GALLERY_FOR_IMAGE_SELECTION_REQUESTCODE) {
                // Cria um bitmap através do URI da imagem selecionada da galeria, e através dele cria e salva uma imagem em Android/data/applicationPackage/files/ImageToPrint.jpg, que será utilizada na impressão de imagem por PATH
                Uri returnUri = data.getData();
                Bitmap bitmapImage = null;

                try {
                    bitmapImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), returnUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //Atualiza a view pela imagem selecionada na galeria
                imageView.setImageBitmap(bitmapImage);

                //Salva a imagem dentro do diretório da aplicação
                storeImage(bitmapImage);
            }
        } else {
            Toast.makeText(this, "Você não escolheu uma imagem!", Toast.LENGTH_LONG).show();
        }
    }


    //Salva uma copia da imagem enviada como bitmap por parametro dentro do diretorio do dispostivo, para a impressao via comando ImprimeImagem
    private void storeImage(Bitmap image) {
        File pictureFile = getCreatedImage();

        //Salva a imagem
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            Log.e("Error", "Arquivo não encontrado: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("Error", "Erro ao acessar o arquivo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    //Cria a imagem que será salva no diretório da aplicação.
    private File getCreatedImage() {
        String rootDirectoryPATH = ActivityUtils.getRootDirectoryPATH(this);

        // A imagem a ser impressa sempre tera o mesmo nome para que a impressão ache o ultimo arquivo salvo
        File mediaFile;
        String mImageName = "ImageToPrint.jpg";
        mediaFile = new File(rootDirectoryPATH + File.separator + mImageName);
        return mediaFile;
    }
}