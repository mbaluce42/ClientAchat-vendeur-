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
        /*try {
            clientSocket = SocketManager.createClientSocket("localhost", "50001"); // PORT_PAYMENT
            protocol = new Prot_BSPP(clientSocket);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Erreur de connexion au serveur", "Erreur", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }*/
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

            if (nom.isEmpty() || prenom.isEmpty())
            {
                JOptionPane.showMessageDialog(this, "Veuillez remplir tous les champs", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            //faire une accès au serveur pour vérifier si le client existe
            //ResultatBSPP resultat = protocol.BSPP_Op("GET_CLIENT#" + nom + "#" + prenom);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erreur de communication avec le serveur", "Erreur", JOptionPane.ERROR_MESSAGE);

        }


    }

    private void handleSearch()
    {
        // faire acces au serveur pour récupérer le livre recherché
        /*try {
            String searchinfo = buildSearchInfoBook();
            ResultatBSPP resultat = protocol.BSPP_Op("SELECT_BOOK#" + searchinfo);

            if (resultat.isSuccess())
            {
                updateBooksTable(resultat.getMessage());
            } else {
                JOptionPane.showMessageDialog(this, "Erreur: " + resultat.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Erreur de communication avec le serveur", "Erreur", JOptionPane.ERROR_MESSAGE);
        }*/
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

    private void handleAddToCart()
    {
        int selectedRow = booksTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un livre", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // Ajouter le livre selectionner au panier -> ducoup faire un acces au serveur pour demander d'ajouter le livre(item) dans le panier du client
        /*try {
            String bookId = booksTable.getValueAt(selectedRow, 0).toString();
            int quantity = (int) quantitySpinner.getValue();

            ResultatBSPP resultat = protocol.BSPP_Op("ADD_CADDY_ITEM#" + bookId + "#" + quantity);

            if (resultat.isSuccess()) {
                updateCartTable();
            } else {
                JOptionPane.showMessageDialog(this, "Erreur: " + resultat.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Erreur de communication avec le serveur", "Erreur", JOptionPane.ERROR_MESSAGE);
        }*/
    }

    private void handleRemoveFromCart()
    {
        int selectedRow = cartTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un article du panier", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // Supprimer l'article selectionner du panier -> ducoup faire un acces au serveur pour demander de supprimer l'article du panier du client
        /*try {
            String bookId = cartTable.getValueAt(selectedRow, 0).toString();
            int quantity = Integer.parseInt(cartTable.getValueAt(selectedRow, 2).toString());

            ResultatBSPP resultat = protocol.BSPP_Op("DEL_CADDY_ITEM#" + bookId + "#" + quantity);

            if (resultat.isSuccess()) {
                updateCartTable();
            } else {
                JOptionPane.showMessageDialog(this, "Erreur: " + resultat.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Erreur de communication avec le serveur", "Erreur", JOptionPane.ERROR_MESSAGE);
        }*/
    }

    private void handleEmptyCart()
    {
        // Vider le panier -> ducoup faire un acces au serveur pour demander de vider le panier du client
        /*try {
            ResultatBSPP resultat = protocol.BSPP_Op("CANCEL_CADDY#" + clientId);

            if (resultat.isSuccess()) {
                cartTableModel.setRowCount(0);
            } else {
                JOptionPane.showMessageDialog(this, "Erreur: " + resultat.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Erreur de communication avec le serveur", "Erreur", JOptionPane.ERROR_MESSAGE);
        }*/
    }

    private void handlePayment()
    {
        // Payer le panier -> ducoup faire un acces au serve pour modif le stock des livres (item)
        /*try {
            ResultatBSPP resultat = protocol.BSPP_Op("PAY_CADDY#" + clientId);

            if (resultat.isSuccess()) {
                JOptionPane.showMessageDialog(this, "Paiement effectué avec succès", "Succès", JOptionPane.INFORMATION_MESSAGE);
                resetForNewClient();
            } else {
                JOptionPane.showMessageDialog(this, "Erreur: " + resultat.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Erreur de communication avec le serveur", "Erreur", JOptionPane.ERROR_MESSAGE);
        }*/
    }

    private void handleCancel()
    {
        handleEmptyCart();
        resetForNewClient();
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

    private void loadAuthorsList()
    {
        // À implémenter: charger la liste des auteurs depuis le serveur
    }

    private void loadSubjectsList()
    {
        // À implémenter: charger la liste des sujets depuis le serveur
    }

    private void updateBooksTable(String data)
    {
        booksTableModel.setRowCount(0);
        // À implémenter: parser les données et les ajouter à la table
    }

    private void updateCartTable()
    {
        cartTableModel.setRowCount(0);
        // À implémenter: récupérer et afficher le contenu du panier
    }

    public static void main(String[] args)
    {
        MainWindowClientAchat frame = new MainWindowClientAchat();
    }
}