package es.empresa.comergallego;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class EditActivity extends AppCompatActivity {

    EditText editTextNombre, editTextApellido, editTextCorreo;
    Button btnGuardarCambios, btnAtras;

    // Datos de conexión a la base de datos PostgreSQL
    //String url = "jdbc:postgresql://ep-shy-glade-57906898.eu-central-1.aws.neon.fl0.io:5432/comergallego";
    //String user = "fl0user";
    //String password = "8Zizcvy1rMhs";

    String url = "jdbc:postgresql://ep-proud-queen-a25i44xx.eu-central-1.aws.neon.tech/comergallego";
    String user = "comergallego_owner";
    String password = "MnexL8Y1OZCc";
    private String[] datosUsuarioArray;

    private Bundle extras;
    private String idUsuario;
    private String paquete;
    private GestorBBDDOperacionesUsuarios bbddUsuarios;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit);

        StrictMode.ThreadPolicy threadPolicy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(threadPolicy);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializar vistas
        editTextNombre = findViewById(R.id.editTextNombre);
        editTextApellido = findViewById(R.id.editTextApellido);
        editTextCorreo = findViewById(R.id.editTextCorreo);
        btnGuardarCambios = findViewById(R.id.btnGuardarCambios);
        btnAtras = (Button) findViewById(R.id.boton2_edit);

        try {
            bbddUsuarios = new GestorBBDDOperacionesUsuarios();
            extras = getIntent().getExtras();
            if (extras!=null) {
                paquete = extras.getString("NOMBREUSUARIO");
                idUsuario = bbddUsuarios.consultaID(paquete);
                String consulta = bbddUsuarios.usuario(idUsuario);

                datosUsuarioArray = consulta.split("-");

                String nombre = datosUsuarioArray[0];
                String apellido = datosUsuarioArray[1];
                String correoElectronico = datosUsuarioArray[2];

                editTextNombre.setText(nombre);
                editTextApellido.setText(apellido);
                editTextCorreo.setText(correoElectronico);

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // Listener para el botón de guardar cambios
        btnGuardarCambios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener los nuevos valores de los campos de texto
                String nuevoNombre = editTextNombre.getText().toString();
                String nuevoApellido = editTextApellido.getText().toString();
                String nuevoCorreo = editTextCorreo.getText().toString();

                // Conectar a la base de datos y actualizar el usuario
                try {
                    Connection connection = DriverManager.getConnection(url, user, password);
                    Statement statement = connection.createStatement();

                    // Obtener el ID del usuario que se está editando (puedes pasarlo como un extra desde la actividad anterior)
                    //String idUsuario = null /* Obt&eacute;n el ID del usuario de alguna manera */;

                    // Actualizar el usuario en la base de datos
                    GestorBBDDOperacionesUsuarios.actualizarNombre(idUsuario, nuevoNombre, statement);
                    GestorBBDDOperacionesUsuarios.actualizarApellido(idUsuario, nuevoApellido, statement);
                    GestorBBDDOperacionesUsuarios.actualizarCorreoElectronico(idUsuario, nuevoCorreo, statement);

                    // Cerrar la conexión y mostrar un mensaje de éxito
                    statement.close();
                    connection.close();
                    Toast.makeText(EditActivity.this, "Usuario actualizado correctamente", Toast.LENGTH_SHORT).show();
                } catch (SQLException e) {
                    e.printStackTrace();
                    Toast.makeText(EditActivity.this, "Error al actualizar usuario", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pasarPantalla = new Intent(EditActivity.this, SearchActivity.class);
                pasarPantalla.putExtra("NOMBREUSUARIO",paquete);
                startActivity(pasarPantalla);
                finish();
            }
        });
    }
}