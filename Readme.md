## Aufgabenstellung

Erweitern Sie die Quiz-App um die Anzeige des Benutzers im Header und die Möglichkeit, nach Abschluss eines Spiels durch Klick auf den entsprechenden Progress-Indikator im Header die entsprechende Frage darzustellen. 

### Teil 1: Anzeige des Benutzers im Header

Im Header der App (``game_fragment.xml``) befindet sich ein TextView für die Anzeige des Benutzers (ID: ``userLabel``). Dieser TextView soll während des Spiels den *Vornamen* des Benutzers anzeigen. 

Dazu bietet das REST API eine Methode an, mit der nach Usern gesucht werden kann (["Search for users"](http://quiz.moarsoft.com:8080/quizapi/#/user/get_user_query)).

Diese können Sie verwenden, um am Server nach Ihrem Benutzer zu suchen - und dann den Vornamen dieses Benutzers als ``text`` Property von ``userLabel`` anzuzeigen.

#### Tasks

* Model
    * Definieren einer Konstante für den Usernamen (``const val username``)
    * Verwenden dieser Konstante anstelle der bisherigen hardcoded Verwendung in den bestehenden calls.
    * Definieren der Klasse User (``data class User``) mit den Properties, wie sie im User-Schema des REST APIs beschrieben sind. 
    * Erweitern des ``QuizRepository`` um eine Methode ``getUser()`` (analog zu ``startGame``)
        * Verwenden der neuen Such-Funktionalität von ``RestApi`` (siehe Task RestApi), um mithilfe der username-Konstante am Server nach Ihrem User zu suchen.
        * Falls der API-Call nicht erfolgreich war: Exception werfen
        * Falls der API-Call erfolgreich war: den User mit Ihrem Username zurückgeben

* RestApi
    * Definieren einer neuen Methode zum Suchen eines User-Objekts mithilfe eines Usernames 
        * Verwenden der Annotation, die zur Beschreibung der [Suchmethode des API](http://quiz.moarsoft.com:8080/quizapi/#/user/get_user_query) passt
        * Verwenden nur eines Parameters (``username``) in der Methode, mittels der ``@Query``-Annotation mappen dieses Parameter auf den Namen des entsprechenden Parameters im API. Die Query-Parameter zum Suchen nach Vorname bzw. Nachname brauchen nicht verwendet werden. 
        * Als Antwort erhält man eine Liste von passenden User-Objekten, Rückgabewert ist daher ``Deferred<Response<List<User>>>``

* ViewModel
    * Definieren einer privaten Variable ``user: User?`` analog zu ``game``
    * Definieren einer neuen beobachtbaren Variable ``userLabel`` (für die Anzeige im Header des View)
    * Definieren einer privaten Hilfsfunktion ``fetchUser()``, die ``runInThread`` verwendet, um ``QuizRepository.getUser()`` aufzurufen
        * Falls erfolgreich: ``user`` den erhaltenen User zuweisen, ``userLabel`` soll den Vornamen dieses Users erhalten
        * Falls nicht erfolgreich: Setzen der ``error`` Property
    * Beim Starten eines neuen Spiels (falls der User nicht bereits vom Server geholt wurde) soll ``fetchUser`` verwendet werden

* View
    * "Beobachten" von ``viewModel.userLabel`` und falls es sich ändert, dem TextView mit der ID ``userLabel`` zuweisen.
 
 
 ### Teil 2: Navigieren durch die Fragen nach Spiel-Abschluss
 
 Im Header befinden sich die Progress-Indikatoren, welche anzeigen, ob und wie die entsprechende Quiz-Frage beantwortet wurde.
 
 Wenn ein Spiel *abgeschlossen* ist (``finished``), soll man durch Klick auf einen Progress-Indikator die entsprechende Frage angezeigt bekommen (inklusive der passenden Button-Backgrounds).
 
 **Bemerkung:** Das Model stellt diese Funktionalität bereits zur Verfügung und bietet dafür die Methode ``gotoIndex(indexToGoTo: Int)`` an.
 
  #### Tasks
  
  * ViewModel
      * Implementieren einer neuen User-Aktion (fun ``selectQuestion(index: Int)``)
          * Dort verwenden der neuen Model-Funktionalität ``gotoIndex``
          * Danach Update der beobachtbaren ``question`` Property 
          * Außerdem Update der ``buttonMarkers`` und ``progressMarkers``
          * Nur, falls das Spiel abgeschlossen ist, ansonsten nichts tun.

* View
    * Ein Klick auf einen Progress-Indikator (``progressIndicators`` ist die Liste all dieser Views) soll die neue ViewModel-Funktionalität verwenden, um zur entsprechenden Frage zu navigieren. 
    * Hinweis: Kotlin stellt die Methode ``forEachIndexed`` zur Verfügung, um durch eine Liste zu iterieren und das Element und den Index als Parameter übergeben zu bekommen (wird zB. im ``GameFragment`` bereits verwendet). 



## Abgabe

Legen Sie Ihr Projekt als zip-Datei in Moodle unter "Abgabe Assignment" ab. (Abgabe ist möglich von Do. 24.06. 00:00 Uhr bis Fr. 02.07. 08:30). 




#### Toi toi toi! 