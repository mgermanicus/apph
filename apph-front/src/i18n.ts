import i18n from 'i18next';
import { initReactI18next } from 'react-i18next';

// the translations
// (tip move them in a JSON file and import them,
// or even better, manage them separated from your code: https://react.i18next.com/guides/multiple-translation-files)
const resources = {
  en: {
    translation: {
      signin: {
        forgottenPassword: 'Forgotten password ?',
        login: 'Login',
        rememberMe: 'Remember me',
        noAccount: "You don't have an account yet? Register now!",
        error: {
          email: 'Invalid Email',
          credentials: 'Login or password incorrect',
          serverConnection: 'Server connection failure'
        }
      },
      signup: {
        error: {
          password: 'These passwords do not match. Please try again.',
          email: 'Invalid email.',
          emailUsed: 'Email already used'
        },
        create: 'Create an account',
        exist: 'Already have an account? Sign in',
        created: 'User created'
      },
      user: {
        email: 'Email',
        login: 'Login',
        firstName: 'First name',
        lastName: 'Last name',
        password: 'Password',
        passwordConfirmation: 'Confirmation password',
        error: {
          passwordNotMatch: 'The passwords do not match',
          notAuthenticated: 'The user is not authenticated',
          sessionBindUserNotExist:
            'The user bound to this session does not exist',
          expiredSession: 'The session has expired. Please log in again',
          loginAlreadyToken: 'This login is already taken',
          notExist: "L'utilisateur n'existe pas"
        }
      },
      photoTable: {
        title: 'Title',
        description: 'Description',
        creationDate: 'Creation date',
        shootingDate: 'Shooting date',
        size: 'Size',
        tags: 'Tags',
        url: 'Url',
        actions: 'Actions',
        error: 'Illegal argument.'
      },
      field: {
        photos: 'My Pictures',
        folders: 'My Folders',
        trips: 'My Trips',
        tags: 'My Tags',
        treatments: 'My Treatments',
        advancedSearch: 'Advanced Search'
      },
      folder: {
        create: 'Create',
        createFolder: 'Create a folder',
        creation: 'Creating a file',
        enterName: 'Enter the folder name:',
        name: 'Name of the folder',
        new: 'New folder',
        moveInto: 'Move into the folder',
        moveTo: 'Move to a folder',
        move: 'Move',
        successMove: 'Moving the photos is finished.',
        error: {
          error: 'Error',
          oneOf: {
            notExist: 'One of the photos does not exist.',
            notBelongUser: 'One of the photos does not belong to the user.',
            alreadyExist: 'One of the photos is already in the folder.',
            existingName:
              'One of the photos has a name that already exists in the destination folder.'
          },
          nullFolder: 'Folder is null !',
          emptyFolder: 'The folder name cannot be empty.',
          existingFolder: 'The file already exists in the current folder.',
          notFound: 'Parent folder not found.',
          unauthorized: "User doesn't have access to this folder.",
          root: 'Cannot create a root folder.',
          userNotExist: 'The user does not exist.',
          parentNotExist: 'Non-existent parent folder.',
          warning: 'Warning',
          notExist: 'The folder does not exist.',
          accessDenied: 'The user does not have access to this folder.',
          titleAlreadyUsed: 'Title already used in the folder.'
        }
      },
      photo: {
        noneSelected: 'No photo selected',
        maySelected: 'Please select the photos to be displayed.',
        warningDeleting:
          'If you confirm, your photos will be permanently deleted',
        diaporama: 'Slideshow',
        addTag: 'Add a new tag',
        add: 'Add a photo',
        title: 'Title of the photo',
        successDelete: 'Deletion successfully completed',
        failDelete: 'An error occurred during deleting'
      },
      token: {
        notValid: 'Token not valid',
        expired: 'Token expired'
      },
      action: {
        continue: 'Continue',
        confirm: 'Confirm',
        cancel: 'Cancel',
        delete: 'Delete',
        confirmDelete: 'Do you confirm the deletion?',
        download: 'Download',
        cancelChange: 'Cancel changes',
        willDisconnected: 'You will be disconnected',
        uploading: 'Uploading',
        upload: 'Upload',
        add: 'Ajouter',
        modify: 'Modify'
      },
      profile: {
        edit: 'Edit profile'
      },
      upload: {
        error: {
          upload: 'An error occurred during the upload',
          wrongFormat: 'The file format is not valid',
          folderNotExist: 'The folder does not exist.'
        },
        success: 'Your file has been successfully uploaded',
        fillField: 'Please fill in this field.'
      },
      download: {
        error: {
          download: 'An error occurred during the download',
          fileNotExist: 'The file does not exist',
          accessDenied:
            'The user is not allowed to access the requested resource'
        }
      }
    }
  },
  fr: {
    translation: {
      signin: {
        forgottenPassword: 'Mot de passe oublié ?',
        login: 'Connexion',
        rememberMe: 'Se souvenir de moi',
        noAccount: "Vous n'avez pas encore de compte ? Enregistrez-vous !",
        error: {
          email: 'Email invalide',
          credentials: 'Email ou mot de passe incorrecte',
          serverConnection: 'Échec de connexion au serveur'
        }
      },
      signup: {
        error: {
          password:
            'Ces mots de passe ne correspondent pas. Veuillez réessayer.',
          email: 'Email invalide',
          emailUsed: 'Email déjà utilisé'
        },
        create: 'Créer un compte',
        exist: 'Vous avez déjà un compte ? Se connecter',
        created: 'Utilisateur crée'
      },
      user: {
        email: 'Email',
        login: 'Identifiant',
        firstName: 'Prénom',
        lastName: 'Nom',
        password: 'Mot de passe',
        passwordConfirmation: 'Confirmation du mot de passe',
        error: {
          passwordNotMatch: 'Les mots de passe de correspondent pas',
          notAuthenticated: "L'utilisateur n'est pas authentifié",
          sessionBindUserNotExist:
            "L'utilisateur lié à cette session n'existe pas",
          expiredSession: 'La session a expiré. Veuillez vous reconnecter',
          loginAlreadyToken: 'This login is already taken',
          notExist: 'User does not exist'
        }
      },
      photoTable: {
        title: 'Titre',
        description: 'Description',
        creationDate: 'Date de création',
        shootingDate: 'Date de prise de vue',
        size: 'Taille',
        tags: 'Tags',
        url: 'Url',
        actions: 'Actions',
        error: 'Argument illégal.'
      },
      field: {
        photos: 'Mes Photos',
        folders: 'Mes Dossiers',
        trips: 'Mes Voyages',
        tags: 'Mes Tags',
        treatments: 'Mes traitements',
        advancedSearch: 'Recherche avancée'
      },
      folder: {
        create: 'Créer',
        createFolder: 'Créer un dossier',
        creation: "Création d'un dossier",
        enterName: 'Entrez le nom de dossier:',
        name: 'Nom du Dossier',
        new: 'Nouveau Dossier',
        moveInto: 'Déplacer dans le dossier',
        moveTo: 'Déplacer vers un dossier',
        move: 'Déplacer',
        successMove: 'Le déplacement des photos est terminé.',
        error: {
          error: 'Error',
          oneOf: {
            photoNotExist: "L'une des photos n'existe pas.",
            notBelongUser: "L'une des photos n'appartient pas à l'utilisateur.",
            alreadyExist: "L'une des photos est déjà dans le dossier.",
            existingName:
              "L'une des photos comporte un nom existant déjà dans le dossier destinataire."
          },
          nullFolder: 'Folder est null !',
          emptyFolder: 'Le nom du dossier ne peux pas être vide',
          existingFolder: 'Le dossier existe déjà dans le dossier actuel.',
          notFound: 'Dossier parent introuvable.',
          unauthorized: "L'utilisateur n'a pas accès à ce dossier.",
          root: 'Impossible de créer un dossier racine.',
          userNotExist: "L'utilisateur n'existe pas.",
          parentNotExist: 'Dossier parent non existant.',
          warning: 'Avertissement',
          notExist: "Le dossier n'existe pas.",
          accessDenied: "L'utilisateur n'a pas accès à ce dossier.",
          titleAlreadyUsed: 'Titre déjà utilisé dans le dossier.'
        }
      },
      photo: {
        noneSelected: 'Aucune photo sélectionnée',
        maySelected: 'Veuillez sélectionner les photos à afficher.',
        warningDeleting:
          'Si vous confirmez, vos photos seront définitivement effacés',
        diaporama: 'Diaporama',
        addTag: 'Ajouter un nouveau tag',
        add: 'Ajouter une photo',
        title: 'Titre de la photo',
        successDelete: 'Suppression effectuée avec succès',
        failDelete: 'Une erreur est survenue lors de la suppression'
      },
      token: {
        notValid: 'Token non valide',
        expired: 'Token expiré'
      },
      action: {
        continue: 'Continuer',
        confirm: 'Valider',
        cancel: 'Annuler',
        delete: 'Supprimer',
        confirmDelete: 'Êtes vous sur de vouloir supprimer <?',
        download: 'Télécharger',
        cancelChange: 'Annuler les modifications',
        willDisconnected: 'Vous allez être deconnecté',
        uploading: 'Téléchargement',
        upload: 'Télécharger',
        add: 'Ajouter',
        modify: 'Modifier'
      },
      profile: {
        edit: 'Modifier le profil'
      },
      upload: {
        error: {
          upload: "Une erreur est survenue lors de l'upload",
          wrongFormat: "Le format du fichier n'est pas valide",
          folderNotExist: "Le dossier n'existe pas."
        },
        success: 'Votre fichier a bien été uploadé',
        fillField: 'Veuillez renseigner ce champ.'
      },
      download: {
        error: {
          download: 'Une erreur est survenue lors du téléchargement',
          fileNotExist: "Le fichier n'existe pas",
          accessDenied:
            "L'utilisateur n'est pas autorisé à accéder à la ressource demandée"
        }
      }
    }
  }
};

i18n
  .use(initReactI18next) // passes i18n down to react-i18next
  .init({
    resources,
    lng: 'fr', // language to use, more information here: https://www.i18next.com/overview/configuration-options#languages-namespaces-resources
    // you can use the i18n.changeLanguage function to change the language manually: https://www.i18next.com/overview/api#changelanguage
    // if you're using a language detector, do not define the lng option

    interpolation: {
      escapeValue: false // react already safes from xss
    }
  });

export default i18n;
