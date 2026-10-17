package com.taller.vista;

import com.taller.excepciones.DatosException;
import com.taller.excepciones.ValidacionException;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.Window;
import java.time.LocalDate;

public class ReporteIngresos extends ReporteBase {

    private final JTextField txtDesde = new JTextField(LocalDate.now().withDayOfMonth(1).toString(), 10);
    private final JTextField txtHasta = new JTextField(LocalDate.now().toString(), 10);

    public ReporteIngresos(Window propietario) {
        super(propietario, "Ingresos por reparaciones");
        panelFiltros.add(new JLabel("Desde (AAAA-MM-DD):"));
        panelFiltros.add(txtDesde);
        panelFiltros.add(new JLabel("Hasta (AAAA-MM-DD):"));
        panelFiltros.add(txtHasta);
    }

    @Override
    protected DefaultTableModel generar() throws ValidacionException, DatosException {
        LocalDate desde = LocalDate.parse(txtDesde.getText().trim());
        LocalDate hasta = LocalDate.parse(txtHasta.getText().trim());
        if (hasta.isBefore(desde)) {
            throw new ValidacionException("La fecha final no puede ser anterior a la inicial");
        }
        return dao.ingresos(desde, hasta);
    }
}
