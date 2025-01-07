package VIEW;

import MODEL.entity.Author;
import MODEL.entity.CaddyItem;
import MODEL.entity.Subject;
import MODEL.entity.Book;
import MODEL.networking.SocketManager;

import MODEL.networking.Prot_BSPP;
import ServeurGeneriqueTCP.reponses.*;
import ServeurGeneriqueTCP.requetes.*;



import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.IOException;
import java.net.Socket;
import java.util.List;


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
        setTitle("Application Achat (vendeur)");
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

    private void setupBooksTable()
    {
        String[] columns = {"Id", "Titre", "Auteur", "Sujet", "ISBN", "Pages","Stock", "Prix", "Année"};
        booksTableModel = new DefaultTableModel(columns, 0)
        {
            @Override
            public boolean isCellEditable(int row, int column)
            {
                return false;
            }
        };
        booksTable.setModel(booksTableModel);
        booksTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private void setupCartTable()
    {
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

            if (nouveauClientCheckBox.isSelected())
            {
                // Créer un nouveau client
                String telephone = JOptionPane.showInputDialog(this, "Numéro de téléphone :");
                String adresse = JOptionPane.showInputDialog(this, "Adresse :");
                String email = JOptionPane.showInputDialog(this, "Email :");

                RequeteAddClient reqAddClient=new RequeteAddClient(nom,prenom,telephone,adresse,email);
                ReponseAddClient repAddClient=(ReponseAddClient)protocol.echangeObject(reqAddClient);

                if (repAddClient instanceof ReponseAddClient== false)
                {
                    JOptionPane.showMessageDialog(this, "Erreur lors de la création du client", "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (repAddClient.isSuccess())
                {
                    clientId = String.valueOf(repAddClient.getIdClient());
                    JOptionPane.showMessageDialog(this, "Client créé avec succès", "Succès", JOptionPane.INFORMATION_MESSAGE);
                }
                else
                {
                    JOptionPane.showMessageDialog(this, "Erreur: " + repAddClient.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            else
            {
                // Vérifier si le client existe
                RequeteGetClient reqGetClient=new RequeteGetClient(nom,prenom);
                ReponseGetClient repGetClient=(ReponseGetClient)protocol.echangeObject(reqGetClient);

                if (repGetClient instanceof ReponseGetClient== false)
                {
                    JOptionPane.showMessageDialog(this, "Erreur lors de la récupération du client", "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (repGetClient.isSuccess())
                {
                    clientId = String.valueOf(repGetClient.getIdClient());
                }
                else
                {
                    JOptionPane.showMessageDialog(this, "Client non trouvé", "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }
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
            updateCartTable();

        } catch (IOException | ClassNotFoundException ex) {
            System.out.println("OK CHEF");
            JOptionPane.showMessageDialog(this, "Erreur de communication avec le serveur", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleSearch()
    {
        try
        {
            //verif chaque champs si il y a quelque chose dedans
            RequeteGetBooks reqGetBooks;
            ReponseGetBooks repGetBooks;

            if(!titreField.getText().isEmpty())
            {
                reqGetBooks=new RequeteGetBooks(titreField.getText(),"NULL","NULL","NULL", 0);
                repGetBooks=(ReponseGetBooks)protocol.echangeObject(reqGetBooks);

                if (repGetBooks instanceof ReponseGetBooks== false)
                {
                    JOptionPane.showMessageDialog(this, "Erreur lors de la récupération des livres", "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (repGetBooks.isSuccess())
                {
                    updateBooksTable(repGetBooks.getBooks());
                }
                else
                {
                    JOptionPane.showMessageDialog(this, "Erreur: " + repGetBooks.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
            else if (!auteurCombo.getSelectedItem().toString().equals("NULL"))
            {
                String firstName=auteurCombo.getSelectedItem().toString().split(" ")[0];

                String lastName=auteurCombo.getSelectedItem().toString().split(" ")[1];

                reqGetBooks=new RequeteGetBooks("NULL",lastName,firstName,"NULL", 0);
                repGetBooks=(ReponseGetBooks)protocol.echangeObject(reqGetBooks);

                if(repGetBooks instanceof ReponseGetBooks== false)
                {
                    JOptionPane.showMessageDialog(this, "Erreur lors de la récupération des livres", "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (repGetBooks.isSuccess())
                {
                    updateBooksTable(repGetBooks.getBooks());
                }
                else
                {
                    JOptionPane.showMessageDialog(this, "Erreur: " + repGetBooks.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
            else if(!sujetCombo.getSelectedItem().toString().equals("NULL"))
            {
                reqGetBooks=new RequeteGetBooks("NULL","NULL","NULL",sujetCombo.getSelectedItem().toString(), 0);
                repGetBooks=(ReponseGetBooks)protocol.echangeObject(reqGetBooks);

                if (repGetBooks instanceof ReponseGetBooks== false)
                {
                    JOptionPane.showMessageDialog(this, "Erreur lors de la récupération des livres", "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (repGetBooks.isSuccess())
                {
                    updateBooksTable(repGetBooks.getBooks());
                }
                else
                {
                    JOptionPane.showMessageDialog(this, "Erreur: " + repGetBooks.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
            else if (Float.parseFloat(prixMaxSpinner.getValue().toString()) != 0.0)
            {
                float prixMax=Float.parseFloat(prixMaxSpinner.getValue().toString());
                reqGetBooks=new RequeteGetBooks("NULL","NULL","NULL","NULL", prixMax);
                repGetBooks=(ReponseGetBooks)protocol.echangeObject(reqGetBooks);

                if (repGetBooks instanceof ReponseGetBooks== false)
                {
                    JOptionPane.showMessageDialog(this, "Erreur lors de la récupération des livres", "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (repGetBooks.isSuccess())
                {
                    updateBooksTable(repGetBooks.getBooks());
                }
                else
                {
                    JOptionPane.showMessageDialog(this, "Erreur: " + repGetBooks.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
            else
            {
                JOptionPane.showMessageDialog(this, "Veuillez remplir au moins un champ", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException | ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(this, "Erreur de communication avec le serveur", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String buildSearchInfoBook()
    {
        StringBuilder info = new StringBuilder();
        info.append(auteurCombo.getSelectedItem()).append("#");
        info.append(sujetCombo.getSelectedItem()).append("#");
        if(titreField.getText().isEmpty())
        {
            info.append("NULL#");
        }
        else
        {
            info.append(titreField.getText()).append("#");
        }
        info.append(prixMaxSpinner.getValue());
        return info.toString();
    }

    private void handleAddToCart()
    {
        int selectedRow = booksTable.getSelectedRow();
        if (selectedRow == -1)
        {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un livre", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            String bookId = booksTable.getValueAt(selectedRow, 0).toString();
            int quantity = (int) quantitySpinner.getValue();

            // Vérifier le stock disponible
            int stockDisponible = Integer.parseInt(booksTable.getValueAt(selectedRow, 6).toString());
            if (quantity > stockDisponible)
            {
                JOptionPane.showMessageDialog(this, "Stock insuffisant", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            RequeteAddCaddyItem reqAddCaddyItem=new RequeteAddCaddyItem(Integer.parseInt(clientId),Integer.parseInt(bookId),quantity);
            ReponseAddCaddyItem repAddCaddyItem=(ReponseAddCaddyItem)protocol.echangeObject(reqAddCaddyItem);

            if (repAddCaddyItem.isSuccess())
            {
                updateCartTable();
                // Mettre à jour le stock dans la table des livres
                booksTableModel.setValueAt(stockDisponible - quantity, selectedRow, 6);
            }
            else
            {
                JOptionPane.showMessageDialog(this, "Erreur: " + repAddCaddyItem.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException | ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(this, "Erreur de communication avec le serveur", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void handleRemoveFromCart()
    {
        int selectedRow = cartTable.getSelectedRow();
        if (selectedRow == -1)
        {
            JOptionPane.showMessageDialog(this,
                    "Veuillez sélectionner un article du panier",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try
        {
            String itemId = cartTable.getValueAt(selectedRow, 0).toString();
            int quantity = Integer.parseInt(cartTable.getValueAt(selectedRow, 2).toString());

            RequeteDelCaddyItem reqDelCaddyItem=new RequeteDelCaddyItem(Integer.parseInt(itemId));
            ReponseDelCaddyItem repDelCaddyItem=(ReponseDelCaddyItem)protocol.echangeObject(reqDelCaddyItem);

            if (repDelCaddyItem.isSuccess())
            {
                // Mettre à jour l'affichage du panier
                updateCartTable();

                JOptionPane.showMessageDialog(this,
                        "Article supprimé du panier",
                        "Succès",
                        JOptionPane.INFORMATION_MESSAGE);
            }
            else
            {
                JOptionPane.showMessageDialog(this,
                        "Erreur: " + repDelCaddyItem.getMessage(),
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            }


        } catch (ClassNotFoundException | IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Erreur de communication avec le serveur",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleEmptyCart()
    {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Voulez-vous vraiment vider le panier ?",
                "Confirmation",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION)
        {
            try
            {
                RequeteCancelCaddy reqCancelCaddy=new RequeteCancelCaddy(Integer.parseInt(clientId));
                ReponseCancelCaddy repCancelCaddy=(ReponseCancelCaddy)protocol.echangeObject(reqCancelCaddy);

                if (repCancelCaddy.isSuccess())
                {
                    // Vider la table du panier
                    cartTableModel.setRowCount(0);

                    JOptionPane.showMessageDialog(this,
                            "Panier vidé avec succès",
                            "Succès",
                            JOptionPane.INFORMATION_MESSAGE);
                }
                else
                {
                    JOptionPane.showMessageDialog(this,
                            "Erreur: " + repCancelCaddy.getMessage(),
                            "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (IOException | ClassNotFoundException ex) {
                JOptionPane.showMessageDialog(this,
                        "Erreur de communication avec le serveur",
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handlePayment()
    {
        // Vérifier si le panier n'est pas vide
        if (cartTableModel.getRowCount() == 0)
        {
            JOptionPane.showMessageDialog(this,
                    "Le panier est vide",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Calculer le total
        float total = 0;

        //le total se trouve au dernier element
        total = Float.parseFloat(cartTableModel.getValueAt(cartTableModel.getRowCount()-1, 4).toString());

        int confirm = JOptionPane.showConfirmDialog(this,
                "Total à payer: " + String.format("%.2f", total) + "€\nConfirmer le paiement ?",
                "Confirmation",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION)
        {
            //entrer les infos de la carte avec verification si information est valide, correcte avec le regex
            String cardName;
            String cardNumber;
            String cardExpiry;
            String cardCVV;
            do {
                cardName = JOptionPane.showInputDialog(this, "Nom du titulaire de la carte(au moins 2 lettres) :");
            } while (!cardName.matches("^[a-zA-Z]+(([',. -][a-zA-Z ])?[a-zA-Z]*)*$"));

            do {
                cardNumber = JOptionPane.showInputDialog(this, "Numéro de carte(16 chiffres) :");
            } while (!cardNumber.matches("^[0-9]{16}$"));


            do {

                cardExpiry = JOptionPane.showInputDialog(this, "Date d'expiration (MM/YYYY) :");
            }while (!cardExpiry.matches("^(0[1-9]|1[0-2])/(20[2-9][0-9])$")); // veux dire que la date doit etre entre 2020 et 2099 et le mois entre 01 et 12

            do {
                cardCVV = JOptionPane.showInputDialog(this, "Code de sécurité (CVV) :");
            }while (!cardCVV.matches("^[0-9]{3}$"));//le cvv doit etre de 3 chiffres


            try
            {
                int idClient = Integer.parseInt(clientId);

                //MAJ le stock des livres avant de payer
                RequeteGetCaddy reqGetCaddy=new RequeteGetCaddy(idClient);
                ReponseGetCaddy repGetCaddy=(ReponseGetCaddy)protocol.echangeObject(reqGetCaddy);

                if (repGetCaddy instanceof ReponseGetCaddy== false)
                {
                    JOptionPane.showMessageDialog(this,
                            "Erreur lors de la récupération du panier",
                            "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if(repGetCaddy.isSuccess()) {
                    System.out.println("Panier récupéré avec succès");

                    RequeteGetCaddyItems reqGetCaddyItems = new RequeteGetCaddyItems(repGetCaddy.getIdCaddy());
                    ReponseGetCaddyItems repGetCaddyItems = (ReponseGetCaddyItems) protocol.echangeObject(reqGetCaddyItems);

                    if (repGetCaddyItems.isSuccess()) {
                        System.out.println("Items du panier récupérés avec succès");

                        for (CaddyItem caddyItem : repGetCaddyItems.getItems()) {
                            System.out.println("Mise à jour du stock pour le livre: " + caddyItem.getBook().getTitle());

                            caddyItem.getBook().setStockQuantity(caddyItem.getBook().getStockQuantity() - caddyItem.getQuantity());
                            RequeteUpdateBook reqUpdateBook = new RequeteUpdateBook(caddyItem.getBook().getId(), caddyItem.getBook().getAuthor().getId(), caddyItem.getBook().getSubject().getId(), caddyItem.getBook().getTitle(), caddyItem.getBook().getIsbn(), caddyItem.getBook().getPageCount(), caddyItem.getBook().getStockQuantity(), caddyItem.getBook().getPrice(), caddyItem.getBook().getPublishYear());
                            ReponseUpdateBook repUpdateBook = (ReponseUpdateBook) protocol.echangeObject(reqUpdateBook);

                            if (repUpdateBook instanceof ReponseUpdateBook == false) {
                                JOptionPane.showMessageDialog(this,
                                        "Erreur lors de la mise à jour du stock pour le livre: " + caddyItem.getBook().getTitle(),
                                        "Erreur",
                                        JOptionPane.ERROR_MESSAGE);
                                return;
                            }

                            if (repUpdateBook.isSuccess()) {
                                System.out.println("Stock mis à jour pour le livre: " + caddyItem.getBook().getTitle());
                            } else {
                                JOptionPane.showMessageDialog(this,
                                        "Erreur lors de la mise à jour du stock pour le livre: " + caddyItem.getBook().getTitle(),
                                        "Erreur",
                                        JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    }
                }


                //Payer le caddy
                RequetePayCaddy reqPayCaddy = new RequetePayCaddy(idClient);
                ReponsePayCaddy repPayCaddy = (ReponsePayCaddy) protocol.echangeObject(reqPayCaddy);

                if (repPayCaddy instanceof ReponsePayCaddy== false)
                {
                    JOptionPane.showMessageDialog(this,
                            "Erreur lors du paiement",
                            "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (repPayCaddy.isSuccess())
                {
                    JOptionPane.showMessageDialog(this,
                            repPayCaddy.getMessage(),
                            "Succès",
                            JOptionPane.INFORMATION_MESSAGE);

                    // Après le paiement, réinitialiser pour un nouveau client
                    resetForNewClient();

                }
                else
                {
                    JOptionPane.showMessageDialog(this,
                            "Erreur: " + repPayCaddy.getMessage(),
                            "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (IOException | ClassNotFoundException ex) {
                JOptionPane.showMessageDialog(this,
                        "Erreur de communication avec le serveur",
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleCancel()
    {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Voulez-vous vraiment annuler la session ?",
                "Confirmation",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION)
        {
            try
            {
                // Si le panier n'est pas vide, on le vide d'abord
                if (cartTableModel.getRowCount() > 0)
                {
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

    private void loadAuthorsList()
    {
        try
        {
            RequeteGetAuthors reqGetAuthors=new RequeteGetAuthors();
            ReponseGetAuthors repGetAuthors=(ReponseGetAuthors)protocol.echangeObject(reqGetAuthors);

            if (repGetAuthors instanceof ReponseGetAuthors== false)
            {
                JOptionPane.showMessageDialog(this, "Erreur lors du chargement des auteurs", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if(repGetAuthors.isSuccess())
            {
                auteurCombo.removeAllItems();
                auteurCombo.addItem("NULL"); // Option par défaut
                for (Author author: repGetAuthors.getAuthors())
                {
                    auteurCombo.addItem(author.getFirstName() + " " + author.getLastName());
                }
            }
            else
            {
                JOptionPane.showMessageDialog(this, repGetAuthors.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException | ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des auteurs", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadSubjectsList()
    {
        try {

            RequeteGetSubjects reqGetSubjects=new RequeteGetSubjects();
            ReponseGetSubjects repGetSubjects=(ReponseGetSubjects)protocol.echangeObject(reqGetSubjects);

            if (repGetSubjects instanceof ReponseGetSubjects== false)
            {
                JOptionPane.showMessageDialog(this, "Erreur lors du chargement des sujets", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if(repGetSubjects.isSuccess())
            {
                sujetCombo.removeAllItems();
                sujetCombo.addItem("NULL"); // Option par défaut
                for (Subject subject: repGetSubjects.getSubjects())
                {
                    sujetCombo.addItem(subject.getName());
                }
            }
            else
            {
                JOptionPane.showMessageDialog(this, repGetSubjects.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }

        } catch (IOException | ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des sujets", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateBooksTable(String data)
    {
        booksTableModel.setRowCount(0);
        System.out.println("(updateBooksTable()): "+data);
        // À implémenter: parser les données et les ajouter à la table
        for (String line : data.split("\n")) //id#authorprenom + author nom#subjectName#title#isbn#pages#quantity#price#publicationDate
        {                                           //BookTable model {"Id", "Titre", "Auteur", "Sujet", "ISBN", "Pages","Stock", "Prix", "Année"};
            String[] parts = line.split("#");
            if (parts.length >= 9)
            {
                Object[] row = {
                        parts[0], // ID
                        parts[3], // Titre
                        parts[1], // Auteur
                        parts[2], // Sujet
                        parts[4], // ISBN
                        parts[5], // Pages
                        parts[6], // Stock
                        parts[7], // Prix
                        parts[8]  // Année

                };
                booksTableModel.addRow(row);
            }
        }
    }

    private void updateBooksTable(List<Book> books)
    {
        booksTableModel.setRowCount(0);
        for (Book book : books)
        {
            Object[] row = {
                    String.valueOf(book.getId()),
                    book.getTitle(),
                    book.getAuthor().getFirstName() + " " + book.getAuthor().getLastName(),
                    book.getSubject().getName(),
                    book.getIsbn(),
                    String.valueOf(book.getPageCount()),
                    String.valueOf(book.getStockQuantity()),
                    String.valueOf(book.getPrice()),
                    String.valueOf(book.getPublishYear())
            };
            booksTableModel.addRow(row);
        }
    }

    private void updateCartTable()
    {
        try
        {
            RequeteGetCaddy reqGetCaddy=new RequeteGetCaddy(Integer.parseInt(clientId));
            ReponseGetCaddy repGetCaddy=(ReponseGetCaddy)protocol.echangeObject(reqGetCaddy);

            if (repGetCaddy.isSuccess())
            {
                RequeteGetCaddyItems reqGetCaddyItems=new RequeteGetCaddyItems(repGetCaddy.getIdCaddy());
                ReponseGetCaddyItems repGetCaddyItems=(ReponseGetCaddyItems)protocol.echangeObject(reqGetCaddyItems);

                if (repGetCaddyItems.isSuccess())
                {
                    cartTableModel.setRowCount(0);
                    for (CaddyItem caddyItem : repGetCaddyItems.getItems())
                    {
                        Object[] row = {
                                caddyItem.getId(), // ID
                                caddyItem.getBook().getTitle(), // Titre
                                caddyItem.getQuantity(), // Quantité
                                caddyItem.getBook().getPrice(), // Prix unitaire
                                caddyItem.getBook().getPrice() * caddyItem.getQuantity() // Total
                        };
                        cartTableModel.addRow(row);
                    }

                    //ajoute la ligne de fin avec que le total
                    Object[] row = {
                            "", // ID
                            "", // Titre
                            "", // Quantité
                            "", // Prix unitaire
                            repGetCaddy.getAmount()// Total
                    };
                    cartTableModel.addRow(row);
                }
                else
                {
                    JOptionPane.showMessageDialog(this, "Erreur lors de la récupération du panier", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }

            /*else
            {
                JOptionPane.showMessageDialog(this, "Erreur lors de la récupération du panier", "Erreur", JOptionPane.ERROR_MESSAGE);
            }*/
        }
        catch (IOException | ClassNotFoundException ex)
        {
            JOptionPane.showMessageDialog(this, "Erreur lors de la récupération du panier", "Erreur", JOptionPane.ERROR_MESSAGE);
        }

    }

    public static void main(String[] args)
    {
        MainWindowClientAchat frame = new MainWindowClientAchat();
    }
}