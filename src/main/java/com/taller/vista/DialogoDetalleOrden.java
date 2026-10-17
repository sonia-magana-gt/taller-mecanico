package com.taller.vista;

import com.taller.dao.OrdenDAO;
import com.taller.dao.RepuestoDAO;
import com.taller.dao.ServicioDAO;
import com.taller.excepciones.DatosException;
import com.taller.excepciones.ValidacionException;
import com.taller.modelo.DetalleRepuesto;
import com.taller.modelo.DetalleServicio;
import com.taller.modelo.EstadoOrden;
import com.taller.modelo.OrdenTrabajo;
import com.taller.modelo.Repuesto;
import com.taller.modelo.Servicio;
import com.taller.util.Comprobante;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Window;

public class DialogoDetalleOrden extends JDialog {

    private final OrdenDAO ordenDAO = new OrdenDAO();
    private final ServicioDAO servicioDAO = new ServicioDAO();
    private final RepuestoDAO repuestoDAO = new RepuestoDAO();
    private final int idOrden;
    private OrdenTrabajo orden;

    private final JLabel lblEncabezado = new JLabel();
    private final JLabel lblCliente = new JLabel();
    private final JLabel lblProblema = new JLabel();
    private final DefaultTableModel modeloServicios = UtilVista.crearModelo("Servicio", "Costo");
    private final DefaultTableModel modeloRepuestos = UtilVista.crearModelo("Repuesto", "Cantidad", "Precio unit.", "Subtotal");
    private final JTable tablaServicios = new JTable(modeloServicios);
    private final JTable tablaRepuestos = new JTable(modeloRepuestos);
    private final JComboBox<Servicio> cmbServicio = new JComboBox<>();
    private final JComboBox<Repuesto> cmbRepuesto = new JComboBox<>();
    private final JSpinner spnCantidad = new JSpinner(new SpinnerNumberModel(1, 1, 100000, 1));
    private final JLabel lblManoObra = new JLabel();
    private final JLabel lblRepuestos = new JLabel();
    private final JLabel lblTotal = new JLabel();
    private final JButton botonAgregarServicio = new JButton("Agregar servicio");
    private final JButton botonQuitarServicio = new JButton("Quitar servicio");
    private final JButton botonAgregarRepuesto = new JButton("Agregar repuesto");
    private final JButton botonQuitarRepuesto = new JButton("Quitar repuesto");
    private final JButton botonIniciar = new JButton("Iniciar (EN PROCESO)");
    private final JButton botonFinalizar = new JButton("Finalizar");
    private final JButton botonCancelar = new JButton("Cancelar orden");
    private final JButton botonComprobante = new JButton("Ver comprobante");

    public DialogoDetalleOrden(Window propietario, int idOrden) {
        super(propietario, "Orden de trabajo No. " + idOrden, ModalityType.APPLICATION_MODAL);
        this.idOrden = idOrden;
        setLayout(new BorderLayout(5, 5));

        JPanel encabezado = new JPanel(new GridLayout(0, 1));
        encabezado.add(lblEncabezado);
        encabezado.add(lblCliente);
        encabezado.add(lblProblema);
        encabezado.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        add(encabezado, BorderLayout.NORTH);

        JPanel centro = new JPanel(new GridLayout(1, 2, 10, 0));
        centro.add(armarPanelServicios());
        centro.add(armarPanelRepuestos());
        add(centro, BorderLayout.CENTER);

        add(armarPanelInferior(), BorderLayout.SOUTH);

        botonAgregarServicio.addActionListener(e -> agregarServicio());
        botonQuitarServicio.addActionListener(e -> quitarServicio());
        botonAgregarRepuesto.addActionListener(e -> agregarRepuesto());
        botonQuitarRepuesto.addActionListener(e -> quitarRepuesto());
        botonIniciar.addActionListener(e -> iniciar());
        botonFinalizar.addActionListener(e -> finalizar());
        botonCancelar.addActionListener(e -> cancelar());
        botonComprobante.addActionListener(e -> Comprobante.mostrar(this, orden));

        cargarCombos();
        cargar();
        setSize(1000, 600);
        setLocationRelativeTo(propietario);
    }

    private JPanel armarPanelServicios() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Servicios"));
        panel.add(new JScrollPane(tablaServicios), BorderLayout.CENTER);
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        acciones.add(cmbServicio);
        acciones.add(botonAgregarServicio);
        acciones.add(botonQuitarServicio);
        panel.add(acciones, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel armarPanelRepuestos() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Repuestos"));
        panel.add(new JScrollPane(tablaRepuestos), BorderLayout.CENTER);
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        acciones.add(cmbRepuesto);
        acciones.add(new JLabel("Cantidad:"));
        acciones.add(spnCantidad);
        acciones.add(botonAgregarRepuesto);
        acciones.add(botonQuitarRepuesto);
        panel.add(acciones, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel armarPanelInferior() {
        JPanel totales = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 5));
        totales.add(lblManoObra);
        totales.add(lblRepuestos);
        totales.add(lblTotal);
        JPanel botones = new JPanel();
        botones.add(botonIniciar);
        botones.add(botonFinalizar);
        botones.add(botonCancelar);
        botones.add(botonComprobante);
        JButton botonCerrar = new JButton("Cerrar");
        botonCerrar.addActionListener(e -> dispose());
        botones.add(botonCerrar);
        JPanel inferior = new JPanel(new GridLayout(0, 1));
        inferior.add(totales);
        inferior.add(botones);
        return inferior;
    }

    private void cargarCombos() {
        try {
            cmbServicio.removeAllItems();
            for (Servicio s : servicioDAO.listar()) {
                cmbServicio.addItem(s);
            }
            cmbRepuesto.removeAllItems();
            for (Repuesto r : repuestoDAO.listar()) {
                cmbRepuesto.addItem(r);
            }
        } catch (DatosException e) {
            UtilVista.mostrarError(this, e.getMessage());
        }
    }

    private void cargar() {
        try {
            orden = ordenDAO.buscarCompleta(idOrden);
        } catch (ValidacionException e) {
            UtilVista.mostrarAviso(this, e.getMessage());
            return;
        } catch (DatosException e) {
            UtilVista.mostrarError(this, e.getMessage());
            return;
        }
        lblEncabezado.setText("Orden No. " + orden.getIdOrden() + "   |   Estado: " + orden.getEstado()
                + "   |   Vehículo: " + orden.getVehiculo() + "   |   Mecánico: " + orden.getMecanico().getNombre());
        lblCliente.setText("Cliente: " + orden.getCliente().getNombre() + " (" + orden.getCliente().getId() + ")");
        lblProblema.setText("Problema: " + orden.getDescripcionProblema());

        modeloServicios.setRowCount(0);
        for (DetalleServicio d : orden.getServicios()) {
            modeloServicios.addRow(new Object[]{d.getDescripcion(), d.getSubtotal()});
        }
        modeloRepuestos.setRowCount(0);
        for (DetalleRepuesto d : orden.getRepuestos()) {
            modeloRepuestos.addRow(new Object[]{d.getRepuesto().getNombre(), d.getCantidad(), d.getPrecioUnitario(),
                    d.getSubtotal()});
        }
        lblManoObra.setText("Mano de obra: Q " + orden.calcularTotalManoObra());
        lblRepuestos.setText("Repuestos: Q " + orden.calcularTotalRepuestos());
        lblTotal.setText("TOTAL: Q " + orden.calcularTotal());
        actualizarBotones();
    }

    private void actualizarBotones() {
        EstadoOrden estado = orden.getEstado();
        boolean enProceso = estado == EstadoOrden.EN_PROCESO;
        botonIniciar.setEnabled(estado == EstadoOrden.PENDIENTE);
        botonCancelar.setEnabled(orden.esModificable());
        botonFinalizar.setEnabled(enProceso);
        botonAgregarServicio.setEnabled(enProceso);
        botonQuitarServicio.setEnabled(enProceso);
        botonAgregarRepuesto.setEnabled(enProceso);
        botonQuitarRepuesto.setEnabled(enProceso);
        botonComprobante.setEnabled(estado == EstadoOrden.FINALIZADA);
    }

    private void refrescarTodo() {
        cargarCombos();
        cargar();
    }

    private void agregarServicio() {
        Servicio servicio = (Servicio) cmbServicio.getSelectedItem();
        if (servicio == null) {
            UtilVista.mostrarAviso(this, "Seleccione un servicio");
            return;
        }
        try {
            ordenDAO.agregarServicio(idOrden, servicio.getId());
            refrescarTodo();
        } catch (ValidacionException e) {
            UtilVista.mostrarAviso(this, e.getMessage());
        } catch (DatosException e) {
            UtilVista.mostrarError(this, e.getMessage());
        }
    }

    private void quitarServicio() {
        int fila = tablaServicios.getSelectedRow();
        if (fila < 0) {
            UtilVista.mostrarAviso(this, "Seleccione un servicio de la tabla");
            return;
        }
        try {
            ordenDAO.quitarServicio(idOrden, orden.getServicios().get(fila).getIdDetalle());
            refrescarTodo();
        } catch (ValidacionException e) {
            UtilVista.mostrarAviso(this, e.getMessage());
        } catch (DatosException e) {
            UtilVista.mostrarError(this, e.getMessage());
        }
    }

    private void agregarRepuesto() {
        Repuesto repuesto = (Repuesto) cmbRepuesto.getSelectedItem();
        if (repuesto == null) {
            UtilVista.mostrarAviso(this, "Seleccione un repuesto");
            return;
        }
        try {
            ordenDAO.agregarRepuesto(idOrden, repuesto.getId(), (Integer) spnCantidad.getValue());
            refrescarTodo();
        } catch (ValidacionException e) {
            UtilVista.mostrarAviso(this, e.getMessage());
        } catch (DatosException e) {
            UtilVista.mostrarError(this, e.getMessage());
        }
    }

    private void quitarRepuesto() {
        int fila = tablaRepuestos.getSelectedRow();
        if (fila < 0) {
            UtilVista.mostrarAviso(this, "Seleccione un repuesto de la tabla");
            return;
        }
        try {
            ordenDAO.quitarRepuesto(idOrden, orden.getRepuestos().get(fila).getIdDetalle());
            refrescarTodo();
        } catch (ValidacionException e) {
            UtilVista.mostrarAviso(this, e.getMessage());
        } catch (DatosException e) {
            UtilVista.mostrarError(this, e.getMessage());
        }
    }

    private void iniciar() {
        try {
            ordenDAO.iniciar(idOrden);
            cargar();
        } catch (ValidacionException e) {
            UtilVista.mostrarAviso(this, e.getMessage());
        } catch (DatosException e) {
            UtilVista.mostrarError(this, e.getMessage());
        }
    }

    private void finalizar() {
        if (orden.getServicios().isEmpty()) {
            UtilVista.mostrarAviso(this, "No se puede finalizar una orden sin servicios registrados");
            return;
        }
        String resumen = "Servicios: " + orden.getServicios().size() + "\nRepuestos: " + orden.getRepuestos().size()
                + "\nTotal a pagar: Q " + orden.calcularTotal() + "\n\n¿Confirmar finalización?";
        if (!UtilVista.confirmar(this, resumen)) {
            return;
        }
        try {
            ordenDAO.finalizar(idOrden);
            cargar();
            Comprobante.mostrar(this, orden);
        } catch (ValidacionException e) {
            UtilVista.mostrarAviso(this, e.getMessage());
        } catch (DatosException e) {
            UtilVista.mostrarError(this, e.getMessage());
        }
    }

    private void cancelar() {
        if (!UtilVista.confirmar(this, "¿Seguro que desea cancelar? Los repuestos regresarán al inventario")) {
            return;
        }
        try {
            ordenDAO.cancelar(idOrden);
            refrescarTodo();
        } catch (ValidacionException e) {
            UtilVista.mostrarAviso(this, e.getMessage());
        } catch (DatosException e) {
            UtilVista.mostrarError(this, e.getMessage());
        }
    }
}
