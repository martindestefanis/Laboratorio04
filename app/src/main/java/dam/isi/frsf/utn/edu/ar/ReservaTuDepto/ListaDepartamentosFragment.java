package dam.isi.frsf.utn.edu.ar.ReservaTuDepto;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import dam.isi.frsf.utn.edu.ar.ReservaTuDepto.utils.BuscarDepartamentosTask;
import java.util.List;

import dam.isi.frsf.utn.edu.ar.ReservaTuDepto.modelo.Departamento;
import dam.isi.frsf.utn.edu.ar.ReservaTuDepto.modelo.DepartamentoDAO;
import dam.isi.frsf.utn.edu.ar.ReservaTuDepto.modelo.MyDatabase;
import dam.isi.frsf.utn.edu.ar.ReservaTuDepto.utils.BusquedaFinalizadaListener;
import dam.isi.frsf.utn.edu.ar.ReservaTuDepto.utils.FormBusqueda;

public class ListaDepartamentosFragment extends Fragment implements BusquedaFinalizadaListener<Departamento>, AdapterView.OnItemLongClickListener {

    private TextView tvEstadoBusqueda;
    private ListView listaAlojamientos;
    private DepartamentoAdapter departamentosAdapter;
    private DepartamentoDAO departamentoDAO;

    public ListaDepartamentosFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_lista_departamentos, container, false);

        listaAlojamientos = (ListView) v.findViewById(R.id.listaAlojamientos);
        tvEstadoBusqueda = (TextView) v.findViewById(R.id.estadoBusqueda);
        listaAlojamientos.setOnItemLongClickListener(this);

        departamentoDAO = MyDatabase.getInstance(this.getActivity()).getDepartamentoDAO();

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        Boolean esBusqueda = false;
        Bundle args = getArguments();
        if(args != null) {
            esBusqueda = args.getBoolean("esBusqueda",false);
        }
        if(esBusqueda){
            final FormBusqueda fb = (FormBusqueda) args.getSerializable("frmBusqueda");
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    new BuscarDepartamentosTask(ListaDepartamentosFragment.this, departamentoDAO.getAll()).execute(fb);
                }
            };
            Thread t = new Thread(r);
            t.start();
            tvEstadoBusqueda.setText("Buscando....");
            tvEstadoBusqueda.setVisibility(View.VISIBLE);
        }
        else{
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    llenarLista(departamentoDAO.getAll());
                }
            };
            Thread t = new Thread(r);
            t.start();
        }
    }

    private void llenarLista(final List<Departamento> listaDepartamentos){
        if(listaDepartamentos.isEmpty()) {
            tvEstadoBusqueda.setText("No se encontraron resultados");
        } else {
            tvEstadoBusqueda.setVisibility(View.GONE);
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            departamentosAdapter = new DepartamentoAdapter(getActivity().getApplicationContext(), listaDepartamentos);
                            listaAlojamientos.setAdapter(departamentosAdapter);
                        }
                    });
                }
            };
            Thread t = new Thread(r);
            t.start();
        }
    }


    @Override
    public void busquedaFinalizada(List<Departamento> listaDepartamento) {
        llenarLista(listaDepartamento);
    }

    @Override
    public void busquedaActualizada(String msg) {
        tvEstadoBusqueda.setText(" Buscando..."+msg);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        Departamento selected = (Departamento) departamentosAdapter.getItem(position);
        assert(selected != null);
        Fragment f = new AltaReservaFragment();
        Bundle argumentos = new Bundle();
        argumentos.putSerializable("departamentoSeleccionado",selected);
        f.setArguments(argumentos);
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.contenido, f)
                .addToBackStack(null)
                .commit();
        return false;
    }

}
