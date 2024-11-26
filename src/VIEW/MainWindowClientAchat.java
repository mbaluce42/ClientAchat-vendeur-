package VIEW;

import MODEL.networking.Prot_BSPP;
import MODEL.networking.ResultatBSPP;
import MODEL.networking.SocketManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.net.Socket;

public class MainWindowClientAchat extends JFrame
{
    private JPanel mainPanel;
    private JTextField nomField, prenomField, clientIdField, titreField;
    private JCheckBox nouveauClientCheckBox;
    private JButton validerClientButton;
    private JComboBox<String> auteurCombo, sujetCombo;
    private JSpinner prixMaxSpinner, quantitySpinner;
    private JButton rechercherButton;
    private JTable booksTable, cartTable;
    private JButton ajouterAuPanierButton;
    private JButton supprimerDuPanierButton;
    private JButton viderPanierButton;
    private JButton payerButton;
    private JButton annulerButton;

    private DefaultTableModel booksTableModel;
    private DefaultTableModel cartTableModel;

    private Socket clientSocket;
    private Prot_BSPP protocol;
    private String clientId = null;

    public MainWindowClientAchat()
    {
        setTitle("Application Achat");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Configuration des spinners
        prixMaxSpinner.setModel(new SpinnerNumberModel(0.0, 0.0, 1000.0, 0.01));
        quantitySpinner.setModel(new SpinnerNumberModel(1, 1, 100, 1));

        // Configuration des tables
        setupBooksTable();
        setupCartTable();

        // Configuration des boutons initiaux
        setInitialState();

        // Ajout des listeners
        setupListeners();


        setContentPane(mainPanel);
        pack();
        setLocationRelativeTo(null);

        // Connexion au serveur
        try {
            clientSocket = SocketManager.createClientSocket("localhost", "50001"); // PORT_PAYMENT
            protocol = new Prot_BSPP(clientSocket);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Erreur de connexion au serveur", "Erreur", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        setVisible(true);
    }

    private void setupBooksTable() {
        String[] columns = {"ID", "Titre", "Auteur", "Sujet", "Prix", "Stock"};
        booksTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        booksTable.setModel(booksTableModel);
        booksTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private void setupCartTable() {
        String[] columns = {"ID", "Titre", "Quantité", "Prix unitaire", "Total"};
        cartTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        cartTable.setModel(cartTableModel);
        cartTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private void setInitialState()
    {
        // Désactiver les contrôles de recherche et panier jusqu'à validation du client
        enableSearchControls(false);
        enableCartControls(false);

        clientIdField.setEditable(false);
    }

    private void enableSearchControls(boolean enable)
    {
        titreField.setEnabled(enable);
        auteurCombo.setEnabled(enable);
        sujetCombo.setEnabled(enable);
        prixMaxSpinner.setEnabled(enable);
        rechercherButton.setEnabled(enable);
        booksTable.setEnabled(enable);
        quantitySpinner.setEnabled(enable);
        ajouterAuPanierButton.setEnabled(enable);
    }

    private void enableCartControls(boolean enable)
    {
        cartTable.setEnabled(enable);
        supprimerDuPanierButton.setEnabled(enable);
        viderPanierButton.setEnabled(enable);
        payerButton.setEnabled(enable);
        annulerButton.setEnabled(enable);
    }

    private void setupListeners()
    {
        validerClientButton.addActionListener(e -> handleClientValidation());
        rechercherButton.addActionListener(e -> handleSearch());
        ajouterAuPanierButton.addActionListener(e -> handleAddToCart());
        supprimerDuPanierButton.addActionListener(e -> handleRemoveFromCart());
        viderPanierButton.addActionListener(e -> handleEmptyCart());
        payerButton.addActionListener(e -> handlePayment());
        annulerButton.addActionListener(e -> handleCancel());
    }

    private void handleClientValidation()
    {
        try {
            String nom = nomField.getText().trim();
            String prenom = prenomField.getText().trim();

            if (nom.isEmpty() || prenom.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Veuillez remplir tous les champs", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (nouveauClientCheckBox.isSelected()) {
                // Créer un nouveau client
                String telephone = JOptionPane.showInputDialog(this, "Numéro de téléphone :");
                String adresse = JOptionPane.showInputDialog(this, "Adresse :");
                String email = JOptionPane.showInputDialog(this, "Email :");

                ResultatBSPP resultat = protocol.BSPP_Client_Op("ADD_CLIENT#" + nom + "#" + prenom + "#" + telephone + "#" + adresse + "#" + email);
                if (resultat.isSuccess()) {
                    clientId = resultat.getMessage();
                    JOptionPane.showMessageDialog(this, "Client créé avec succès", "Succès", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Erreur: " + resultat.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } else {
                // Vérifier si le client existe
                ResultatBSPP resultat = protocol.BSPP_Client_Op("GET_CLIENT#" + nom + "#" + prenom);
                if (!resultat.isSuccess()) {
                    JOptionPane.showMessageDialog(this, "Client non trouvé", "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                clientId = resultat.getMessage();
            }

            // Activer les contrôles de recherche et panier
            enableSearchControls(true);
            enableCartControls(true);

            // Désactiver les champs client
            nomField.setEnabled(false);
            prenomField.setEnabled(false);
            nouveauClientCheckBox.setEnabled(false);
            validerClientButton.setEnabled(false);

            clientIdField.setText(clientId);

            // Charger les listes
            loadAuthorsList();
            loadSubjectsList();

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Erreur de communication avec le serveur", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleSearch() {
        try {
            String searchInfo = buildSearchInfoBook();
            ResultatBSPP resultat = protocol.BSPP_Client_Op("GET_BOOKS#" + searchInfo);

            if (resultat.isSuccess()) {
                updateBooksTable(resultat.getMessage());
            } else {
                JOptionPane.showMessageDialog(this, "Erreur: " + resultat.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Erreur de communication avec le serveur", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String buildSearchInfoBook()
    {
        StringBuilder info = new StringBuilder();
        info.append(titreField.getText().trim()).append("#");
        info.append(auteurCombo.getSelectedItem()).append("#");
        info.append(sujetCombo.getSelectedItem()).append("#");
        info.append(prixMaxSpinner.getValue());
        return info.toString();
    }

    private void handleAddToCart() {
        int selectedRow = booksTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un livre", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            String bookId = booksTable.getValueAt(selectedRow, 0).toString();
            int quantity = (int) quantitySpinner.getValue();

            // Vérifier le stock disponible
            int stockDisponible = Integer.parseInt(booksTable.getValueAt(selectedRow, 5).toString());
            if (quantity > stockDisponible) {
                JOptionPane.showMessageDialog(this, "Stock insuffisant", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            ResultatBSPP resultat = protocol.BSPP_Client_Op("ADD_CADDY_ITEM#" + clientId + "#" + bookId + "#" + quantity);
            if (resultat.isSuccess()) {
                updateCartTable();
                // Mettre à jour le stock dans la table des livres
                booksTableModel.setValueAt(stockDisponible - quantity, selectedRow, 5);
            } else {
                JOptionPane.showMessageDialog(this, "Erreur: " + resultat.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Erreur de communication avec le serveur", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void handleRemoveFromCart() {
        int selectedRow = cartTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez sélectionner un article du panier",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            String itemId = cartTable.getValueAt(selectedRow, 0).toString();
            int quantity = Integer.parseInt(cartTable.getValueAt(selectedRow, 2).toString());

            ResultatBSPP resultat = protocol.BSPP_Client_Op("DEL_CADDY_ITEM#" + itemId);
            if (resultat.isSuccess()) {
                // Mettre à jour le panier
                updateCartTable();

                // Mettre à jour le stock dans la table des livres
                for (int i = 0; i < booksTableModel.getRowCount(); i++) {
                    if (booksTableModel.getValueAt(i, 0).toString().equals(itemId)) {
                        int currentStock = Integer.parseInt(booksTableModel.getValueAt(i, 5).toString());
                        booksTableModel.setValueAt(currentStock + quantity, i, 5);
                        break;
                    }
                }

                JOptionPane.showMessageDialog(this,
                        "Article supprimé du panier",
                        "Succès",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Erreur: " + resultat.getMessage(),
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Erreur de communication avec le serveur",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleEmptyCart() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Voulez-vous vraiment vider le panier ?",
                "Confirmation",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                ResultatBSPP resultat = protocol.BSPP_Client_Op("CANCEL_CADDY#" + clientId);
                if (resultat.isSuccess()) {
                    // Remettre à jour les stocks dans la table des livres
                    for (int i = 0; i < cartTableModel.getRowCount(); i++) {
                        String itemId = cartTableModel.getValueAt(i, 0).toString();
                        int quantity = Integer.parseInt(cartTableModel.getValueAt(i, 2).toString());

                        // Mettre à jour le stock dans la table des livres
                        for (int j = 0; j < booksTableModel.getRowCount(); j++) {
                            if (booksTableModel.getValueAt(j, 0).toString().equals(itemId)) {
                                int currentStock = Integer.parseInt(booksTableModel.getValueAt(j, 5).toString());
                                booksTableModel.setValueAt(currentStock + quantity, j, 5);
                                break;
                            }
                        }
                    }

                    // Vider la table du panier
                    cartTableModel.setRowCount(0);

                    JOptionPane.showMessageDialog(this,
                            "Panier vidé avec succès",
                            "Succès",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Erreur: " + resultat.getMessage(),
                            "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this,
                        "Erreur de communication avec le serveur",
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handlePayment() {
        // Vérifier si le panier n'est pas vide
        if (cartTableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                    "Le panier est vide",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Calculer le total
        float total = 0;
        for (int i = 0; i < cartTableModel.getRowCount(); i++) {
            total += Float.parseFloat(cartTableModel.getValueAt(i, 4).toString());
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Total à payer: " + String.format("%.2f", total) + "€\nConfirmer le paiement ?",
                "Confirmation",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                ResultatBSPP resultat = protocol.BSPP_Client_Op("PAY_CADDY#" + clientId);
                if (resultat.isSuccess()) {
                    JOptionPane.showMessageDialog(this,
                            "Paiement effectué avec succès",
                            "Succès",
                            JOptionPane.INFORMATION_MESSAGE);

                    // Après le paiement, réinitialiser pour un nouveau client
                    resetForNewClient();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Erreur: " + resultat.getMessage(),
                            "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this,
                        "Erreur de communication avec le serveur",
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleCancel() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Voulez-vous vraiment annuler la session ?",
                "Confirmation",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // Si le panier n'est pas vide, on le vide d'abord
                if (cartTableModel.getRowCount() > 0) {
                    handleEmptyCart();
                }

                // Réinitialiser l'interface pour un nouveau client
                resetForNewClient();

                JOptionPane.showMessageDialog(this,
                        "Session annulée",
                        "Information",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Erreur lors de l'annulation de la session",
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void resetForNewClient()
    {
        // Réinitialiser les champs
        nomField.setText("");
        prenomField.setText("");
        clientIdField.setText("");
        titreField.setText("");
        prixMaxSpinner.setValue(0.0);
        quantitySpinner.setValue(1);
        nouveauClientCheckBox.setSelected(false);

        // Vider les tables
        booksTableModel.setRowCount(0);
        cartTableModel.setRowCount(0);

        // Réinitialiser les états
        setInitialState();

        // Réactiver les champs client
        nomField.setEnabled(true);
        prenomField.setEnabled(true);
        nouveauClientCheckBox.setEnabled(true);
        validerClientButton.setEnabled(true);

        clientId = null;
    }

    private void loadAuthorsList() {
        try {
            ResultatBSPP resultat = protocol.BSPP_Client_Op("GET_AUTHORS");
            if (resultat.isSuccess()) {
                auteurCombo.removeAllItems();
                auteurCombo.addItem("NULL"); // Option par défaut
                String[] lines = resultat.getMessage().split("\n");
                for (String line : lines) {
                    String[] fields = line.split("#");
                    if (fields.length >= 3) {
                        auteurCombo.addItem(fields[2] + " " + fields[1]); // prénom + nom
                    }
                }
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des auteurs", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadSubjectsList() {
        try {
            ResultatBSPP resultat = protocol.BSPP_Client_Op("GET_SUBJECTS");
            if (resultat.isSuccess()) {
                sujetCombo.removeAllItems();
                sujetCombo.addItem("NULL"); // Option par défaut
                String[] lines = resultat.getMessage().split("\n");
                for (String line : lines) {
                    String[] fields = line.split("#");
                    if (fields.length >= 2) {
                        sujetCombo.addItem(fields[1]); // nom du sujet
                    }
                }
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des sujets", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateBooksTable(String data)
    {
        booksTableModel.setRowCount(0);
        // À implémenter: parser les données et les ajouter à la table
    }

    private void updateCartTable() {
        try {
            ResultatBSPP resultat = protocol.BSPP_Client_Op("GET_CADDY_ITEMS#" + clientId);
            if (resultat.isSuccess()) {
                cartTableModel.setRowCount(0);
                String[] lines = resultat.getMessage().split("\n");
                for (String line : lines) {
                    String[] fields = line.split("#");
                    if (fields.length >= 4) {
                        float prixUnitaire = Float.parseFloat(fields[3]);
                        int quantite = Integer.parseInt(fields[2]);
                        Object[] row = {
                                fields[0], // ID
                                fields[1], // Titre
                                quantite,  // Quantité
                                prixUnitaire, // Prix unitaire
                                prixUnitaire * quantite // Total
                        };
                        cartTableModel.addRow(row);
                    }
                }
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Erreur de communication avec le serveur", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args)
    {
        MainWindowClientAchat frame = new MainWindowClientAchat();
    }
}