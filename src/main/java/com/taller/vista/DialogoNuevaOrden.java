package com.taller.vista;

import com.taller.dao.ClienteDAO;
import com.taller.dao.MecanicoDAO;
import com.taller.dao.OrdenDAO;
import com.taller.dao.VehiculoDAO;
import com.taller.excepciones.DatosException;
import com.taller.excepciones.ValidacionException;
import com.taller.modelo.Cliente;
import com.taller.modelo.Mecanico;
import com.taller.modelo.Vehiculo;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Window;

public class DialogoNuevaOrden extends JDialog {

    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final VehiculoDAO vehiculoDAO = new VehiculoDAO();
    private final MecanicoDAO mecanicoDAO = new MecanicoDAO();
    private final OrdenDAO ordenDAO = new OrdenDAO();
    private final JTextField txtIdCliente = new JTextField(15);
    private final JLabel lblCliente = new JLabel("(busque un cliente)");
    private final JComboBox<Vehiculo> cmbVehiculo = new JComboBox<>();
    private final JTextArea txtProblema = new JTextArea(5, 30);
    private final JComboBox<Mecanico> cmbMecanico = new JComboBox<>();
    private Cliente clienteActual;

    public DialogoNuevaOrden(Window propietario) {
        super(propietario, "Nueva orden de trabajo", ModalityType.APPLICATION_MODAL);
        setLayout(new BorderLayout(5, 5));

        JPanel busqueda = new JPanel(new FlowLayout(FlowLayout.LEFT));
        busqueda.add(new JLabel("ID cliente:"));
        busqueda.add(txtIdCliente);
        JButton botonBuscar = new JButton("Buscar");
        botonBuscar.addActionListener(e -> buscarCliente());
        busqueda.add(botonBuscar);
        busqueda.add(lblCliente);

        JPanel vehiculo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        vehiculo.add(new JLabel("Vehículo:"));
        vehiculo.add(cmbVehiculo);
        JButton botonVehiculo = new JButton("Registrar vehículo");
        botonVehiculo.addActionListener(e -> registrarVehiculo());
        vehiculo.add(botonVehiculo);

        JPanel mecanico = new JPanel(new FlowLayout(FlowLayout.LEFT));
        mecanico.add(new JLabel("Mecánico:"));
        mecanico.add(cmbMecanico);

        JPanel superior = new JPanel(new GridLayout(0, 1));
        superior.add(busqueda);
        superior.add(vehiculo);
        superior.add(mecanico);
        add(superior, BorderLayout.NORTH);

        JPanel problema = new JPanel(new BorderLayout());
        problema.add(new JLabel("Descripción del problema:"), BorderLayout.NORTH);
        txtProblema.setLineWrap(true);
        txtProblema.setWrapStyleWord(true);
        problema.add(new JScrollPane(txtProblema), BorderLayout.CENTER);
        add(problema, BorderLayout.CENTER);

        JPanel botones = new JPanel();
        JButton botonCrear = new JButton("Crear orden");
        botonCrear.addActionListener(e -> crearOrden());
        JButton botonCancelar = new JButton("Cancelar");
        botonCancelar.addActionListener(e -> dispose());
        botones.add(botonCrear);
        botones.add(botonCancelar);
        add(botones, BorderLayout.SOUTH);

        cargarMecanicos();
        pack();
        setMinimumSize(getSize());
        setLocationRelativeTo(propietario);
    }

    private void cargarMecanicos() {
        try {
            cmbMecanico.removeAllItems();
            for (Mecanico m : mecanicoDAO.listar()) {
                cmbMecanico.addItem(m);
            }
        } catch (DatosException e) {
            UtilVista.mostrarError(this, e.getMessage());
        }
    }

    private void cargarVehiculos() {
        try {
            cmbVehiculo.removeAllItems();
            for (Vehiculo v : vehiculoDAO.listarPorCliente(clienteActual.getId())) {
                cmbVehiculo.addItem(v);
            }
        } catch (DatosException e) {
            UtilVista.mostrarError(this, e.getMessage());
        }
    }

    private void buscarCliente() {
        String id = txtIdCliente.getText().trim();
        if (id.isEmpty()) {
            UtilVista.mostrarAviso(this, "Ingrese el identificador del cliente");
            return;
        }
        try {
            Cliente cliente = clienteDAO.buscarPorId(id);
            if (cliente == null) {
                if (!UtilVista.confirmar(this, "El cliente no existe. ¿Desea registrarlo?")) {
                    return;
                }
                cliente = registrarCliente(id);
                if (cliente == null) {
                    return;
                }
            }
            clienteActual = cliente;
            lblCliente.setText(cliente.getNombre());
            cargarVehiculos();
        } catch (DatosException e) {
            UtilVista.mostrarError(this, e.getMessage());
        }
    }

    private Cliente registrarCliente(String id) {
        String nombre = JOptionPane.showInputDialog(this, "Nombre del cliente:");
        if (nombre == null) {
            return null;
        }
        String telefono = JOptionPane.showInputDialog(this, "Teléfono:");
        if (telefono == null) {
            return null;
        }
        String direccion = JOptionPane.showInputDialog(this, "Dirección:");
        if (direccion == null) {
            return null;
        }
        try {
            Cliente cliente = new Cliente(id, nombre, telefono, direccion);
            clienteDAO.insertar(cliente);
            UtilVista.mostrarInfo(this, "Cliente registrado correctamente");
            return cliente;
        } catch (ValidacionException e) {
            UtilVista.mostrarAviso(this, e.getMessage());
        } catch (DatosException e) {
            UtilVista.mostrarError(this, e.getMessage());
        }
        return null;
    }

    private void registrarVehiculo() {
        if (clienteActual == null) {
            UtilVista.mostrarAviso(this, "Primero busque o registre al cliente");
            return;
        }
        String placa = JOptionPane.showInputDialog(this, "Placa:");
        if (placa == null) {
            return;
        }
        String marca = JOptionPane.showInputDialog(this, "Marca:");
        if (marca == null) {
            return;
        }
        String modelo = JOptionPane.showInputDialog(this, "Modelo:");
        if (modelo == null) {
            return;
        }
        String anio = JOptionPane.showInputDialog(this, "Año:");
        if (anio == null) {
            return;
        }
        String color = JOptionPane.showInputDialog(this, "Color:");
        if (color == null) {
            return;
        }
        try {
            Vehiculo vehiculo = new Vehiculo(placa, marca, modelo, UtilVista.leerEntero(anio, "el año"), color,
                    clienteActual.getId());
            vehiculoDAO.insertar(vehiculo);
            cargarVehiculos();
            cmbVehiculo.setSelectedItem(vehiculoSeleccionable(vehiculo.getPlaca()));
        } catch (ValidacionException e) {
            UtilVista.mostrarAviso(this, e.getMessage());
        } catch (DatosException e) {
            UtilVista.mostrarError(this, e.getMessage());
        }
    }

    private Vehiculo vehiculoSeleccionable(String placa) {
        for (int i = 0; i < cmbVehiculo.getItemCount(); i++) {
            if (cmbVehiculo.getItemAt(i).getPlaca().equals(placa)) {
                return cmbVehiculo.getItemAt(i);
            }
        }
        return null;
    }

    private void crearOrden() {
        Vehiculo vehiculo = (Vehiculo) cmbVehiculo.getSelectedItem();
        Mecanico mecanico = (Mecanico) cmbMecanico.getSelectedItem();
        if (clienteActual == null || vehiculo == null) {
            UtilVista.mostrarAviso(this, "Debe seleccionar un cliente y un vehículo");
            return;
        }
        if (mecanico == null) {
            UtilVista.mostrarAviso(this, "Debe seleccionar un mecánico");
            return;
        }
        try {
            int id = ordenDAO.crear(vehiculo.getPlaca(), mecanico.getId(), txtProblema.getText());
            UtilVista.mostrarInfo(this, "Orden No. " + id + " creada en estado PENDIENTE");
            dispose();
        } catch (ValidacionException e) {
            UtilVista.mostrarAviso(this, e.getMessage());
        } catch (DatosException e) {
            UtilVista.mostrarError(this, e.getMessage());
        }
    }
}
