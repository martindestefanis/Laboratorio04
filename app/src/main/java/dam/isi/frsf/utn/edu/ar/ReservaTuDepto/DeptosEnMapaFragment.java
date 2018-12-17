package dam.isi.frsf.utn.edu.ar.ReservaTuDepto;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.List;

import dam.isi.frsf.utn.edu.ar.ReservaTuDepto.modelo.Ciudad;
import dam.isi.frsf.utn.edu.ar.ReservaTuDepto.modelo.CiudadDAO;
import dam.isi.frsf.utn.edu.ar.ReservaTuDepto.modelo.MyDatabase;


public class DeptosEnMapaFragment extends Fragment {
    private Spinner cmbCiudad;
    private CiudadDAO ciudadDAO;
    private List<Ciudad> ciudades;
    private ArrayAdapter<Ciudad> adapterCiudad;

    public DeptosEnMapaFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_deptos_en_mapa, container, false);

        cmbCiudad = (Spinner) v.findViewById(R.id.spinnerCiudad);
        ciudadDAO = MyDatabase.getInstance(this.getActivity()).getCiudadDAO();

        Runnable r = new Runnable() {
            @Override
            public void run() {
                ciudades = ciudadDAO.getAll();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapterCiudad = new ArrayAdapter<Ciudad>(getContext(), android.R.layout.simple_spinner_item, ciudades);
                        cmbCiudad.setAdapter(adapterCiudad);
                        cmbCiudad.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                                Ciudad item = (Ciudad) parent.getItemAtPosition(pos);
                                Fragment f = new MapaFragment();
                                Bundle args = new Bundle();
                                // setear los parametros tipo_mapa y idDepto en el Bundle args
                                args.putInt("tipo_mapa", 3);
                                args.putInt("idCiudad", item.getId()); //VER
                                f.setArguments(args);
                                getActivity().getSupportFragmentManager()
                                        .beginTransaction()
                                        .replace(R.id.contenido, f)
                                        .addToBackStack(null)
                                        .commit();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    }
                });
            }
        };
        Thread t = new Thread(r);
        t.start();

        return v;
    };

}
