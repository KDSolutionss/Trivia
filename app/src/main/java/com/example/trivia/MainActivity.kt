package com.example.trivia

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ExitToApp
import androidx.compose.material.Icon
import androidx.compose.material.icons.filled.Lock
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.trivia.data.*
import com.example.trivia.database.QuestionEntity
import com.example.trivia.database.QuestionFirebase
import com.example.trivia.game.Processor
import com.example.trivia.ui.theme.TriviaTheme
import com.example.trivia.util.LoadingState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        auth = Firebase.auth
//        val factory = ViewModelFactory(this.application)
        val viewModelFirebase = FireBaseViewModel()
        viewModelFirebase.go()
//        val viewModel = factory.let { ViewModelProvider(this, it)[MyViewModel::class.java] }
        val processor = Processor()
        setContent {
            TriviaTheme {

                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    TriviaApp(modifier = Modifier,viewModelFirebase,processor)
                }
            }
        }
    }
}

@Composable
fun SignUpScreen(
    viewModel: SignUpViewModel,
    onNextButtonClicked: () -> Unit,
    signInClicked: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    val state by viewModel.loadingState.collectAsState()
    var isPasswordVisible by remember { mutableStateOf(false) }
    val isFormValid = remember(email, password, confirmPassword) {
        email.isNotBlank() && password.isNotBlank() && confirmPassword == password
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.padding(16.dp)
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.padding(16.dp),
            trailingIcon = {
                IconButton(
                    onClick = { isPasswordVisible = !isPasswordVisible },
                    content = {
                        if (isPasswordVisible)
                            Icon(imageVector = ImageVector.vectorResource
                                (id = R.drawable.baseline_visibility_off_24), contentDescription = "Visibility")
                        else
                            Icon(imageVector = ImageVector.vectorResource
                                (id = R.drawable.baseline_visibility_24), contentDescription = "Visibility")
                    }
                )
            },
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation()
        )

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm password") },
            modifier = Modifier.padding(16.dp),
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation()
        )

        Button(
            onClick = { viewModel.signUp(email, password) },
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            enabled = isFormValid
        ) {
            Text("Sign Up")
        }

        TextButton(
            onClick = { signInClicked() },
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Already have an account? Sign In")

        }
    }
    when (state.status) {
        LoadingState.Status.SUCCESS -> {
            Toast.makeText(LocalContext.current,"Success",Toast.LENGTH_LONG).show()
            onNextButtonClicked()
        }
        LoadingState.Status.FAILED -> {
            Text(text = state.msg ?: "Error")
        }
        else -> {}
    }

}

@Composable
fun TriviaApp(modifier: Modifier, vm: FireBaseViewModel, proc:Processor) {
    val navController = rememberNavController()
    Scaffold { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "sign in",
            modifier = modifier.padding(innerPadding)
        ) {
            composable(route = "sign in")
            {
                LoginScreen(viewModel = AuthViewModel(),{ navController.navigate("main screen") },{ navController.navigate("sign up") })
            }
            composable(route = "sign up")
            {
                SignUpScreen(viewModel = SignUpViewModel(),{ navController.navigate("sign in") },{navController.navigate("sign in")})
            }
            composable(route="main screen")
            {
                Greeting() { navController.navigate("game") }
            }
            composable(route="game")
            {
                MyScreen(vm = vm, processor = proc ) { navController.navigate("signUp") }
            }
        }
    }
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun LoginScreen(viewModel: AuthViewModel,onNextButtonClicked: () -> Unit,signUPClicked : ()->Unit) {

    var userEmail by remember { mutableStateOf("") }
    var userPassword by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val state by viewModel.loadingState.collectAsState()

    // Equivalent of onActivityResult
    Scaffold(
        scaffoldState = rememberScaffoldState(snackbarHostState = snackbarHostState),
        topBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                TopAppBar(
                    backgroundColor = Color.White,
                    elevation = 1.dp,
                    title = {
                        Text(text = "Login")
                    },
                    navigationIcon = {
                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(
                                imageVector = Icons.Rounded.ArrowBack,
                                contentDescription = null,
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { Firebase.auth.signOut() }) {
                            Icon(
                                imageVector = Icons.Rounded.ExitToApp,
                                contentDescription = null,
                            )
                        }
                    }
                )
                if (state.status == LoadingState.Status.RUNNING) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
            }
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(),
                verticalArrangement = Arrangement.spacedBy(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                content = {
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = userEmail,
                        label = {
                            Text(text = "Email")
                        },
                        onValueChange = {
                            userEmail = it
                        }
                    )

                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        value = userPassword,
                        label = {
                            Text(text = "Password")
                        },
                        onValueChange = {
                            userPassword = it
                        },
                        trailingIcon = {
                            IconButton(
                                onClick = { isPasswordVisible = !isPasswordVisible },
                                content = {
                                    if (isPasswordVisible)
                                    Icon(imageVector = ImageVector.vectorResource
                                        (id = R.drawable.baseline_visibility_off_24), contentDescription = "Visibility")
                                    else
                                        Icon(imageVector = ImageVector.vectorResource
                                            (id = R.drawable.baseline_visibility_24), contentDescription = "Visibility")
                                }
                            )
                        },

                    )

                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        enabled = userEmail.isNotEmpty() && userPassword.isNotEmpty(),
                        content = {
                            Text(text = "Login")
                        },
                        onClick = {
                            viewModel.signInWithEmailAndPassword(
                                userEmail.trim(),
                                userPassword.trim()
                            )
                        }
                    )

                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.caption,
                        text = "Login with"
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                    TextButton(
                        onClick = { signUPClicked() },
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text("Need to Sign-up?")

                    }



                    when (state.status) {
                        LoadingState.Status.SUCCESS -> {
                            Toast.makeText(LocalContext.current,"Success",Toast.LENGTH_LONG).show()
                            onNextButtonClicked()
                        }
                        LoadingState.Status.VERIFICATION_WAIT-> {
                            Toast.makeText(LocalContext.current,"Please verify your email",Toast.LENGTH_LONG).show()
                        }
                        LoadingState.Status.FAILED -> {
                            Text(text = state.msg ?: "Error")
                        }
                        else -> {}
                    }
                }
            )
        })
}


@Composable
fun Greeting(modifier: Modifier = Modifier,onNextButtonClicked: () -> Unit) {
    Column(
        modifier.fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(text = "Welcome to Trivia App", fontWeight = FontWeight.Bold)
        Image(
            painter = painterResource(id = R.drawable.clover_svgrepo_com),
            contentDescription = null,
            modifier = modifier
                .padding(20.dp)
                .size(100.dp)
        )
        Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier.fillMaxHeight(1f)) {
            Button(
                onClick = { /*TODO*/ }, modifier = modifier
                    .weight(1f)
                    .padding(10.dp)
            )
            {
                Text("Play offline")
            }
            Button(
                onClick = { onNextButtonClicked() }, modifier = modifier
                    .weight(1f)
                    .padding(10.dp)
            ) {
                Text(text = "Play online")

            }
        }

    }

}

@Composable
fun MyComposable(vm: MyViewModel) {
    val context = LocalContext.current
    var ready = false
    vm.getIsDatabaseCreated().observe(LocalLifecycleOwner.current) { isCreated ->
        ready = isCreated
    }
    if (!ready) {
        // Show a loading screen while the database is being created
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        // Show the data from the database
        val myDao = vm.db.QuestionsDAO()
        val data = myDao.getQuestion().value

        // Use a LazyColumn to display the data
        if (data != null) {
            Text(text = data.answer)
        }
    }
}

//    if (myDb.value == null) {
//        // Show a loading screen while the database is being created
//        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//            CircularProgressIndicator()
//        }
//    } else {
//    // Show the data from the database
//    val myDao = myDb.value!!.QuestionsDAO()
//
//    val data = remember {
//        mutableStateOf(QuestionEntity("", ""))
//    }
//    LaunchedEffect(Unit)
//    {
//        data.value = myDao.getQuestion().value!!
//    }
//
//    // Use a LazyColumn to display the data
//    Text(text = data.value.answer)
//}


@Composable
fun MyScreen(
    modifier: Modifier = Modifier, vm: FireBaseViewModel, processor: Processor,
    onNextButtonClicked: () -> Unit) {
    val focusManager = LocalFocusManager.current
    val text = remember { mutableStateOf("") }
    var progress by remember { mutableStateOf(0.0f) }
    var textFieldValue by remember { mutableStateOf("") }
    val pair = remember { mutableStateOf<QuestionFirebase>(QuestionFirebase("aboba", "aboba")) }
    LaunchedEffect(true)
    {
        vm.go()
        pair.value= vm._entity.value!!
        processor.question = pair.value.question.toString()
        processor.answer = pair.value.answer.toString()
    }

    fun update() {
        vm.go()
        pair.value= vm._entity.value!!
        processor.answer = pair.value.answer.toString()
        processor.question = pair.value.question.toString()
        textFieldValue = " "
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = processor.question,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )


        Text(
            text = if (text.value == "") processor.get_Cipher() else text.value,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Текущий счет ${processor.score}",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = textFieldValue,
            onValueChange = { textFieldValue = it },
            label = { Text("“Введите ответ”") },
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done)

        )
        Spacer(modifier = Modifier.height(16.dp))
        LinearProgressIndicator(progress)
        LaunchedEffect(Unit) {
            while (true) {
                delay(1000L)
                progress += 0.01f
                if (progress >= 1.0f) {
                    update()
                    progress = 0.0f
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { text.value = processor.getHint().first }) {
            Text(text = "Взять подсказку")
        }
        Button(onClick = { text.value = processor.get_mixed().first }) {
            Text(text = "Перемешать ответ")
        }
        Button(onClick = { update();progress = 0.0f;processor.erase_counter() }) {
            Text(text = "Следующий вопрос")
        }
        Button(onClick = {
            processor.tryAnswer = textFieldValue
            if (processor.isAnswerRight()) {
                update()
                progress=0.0f
                text.value=processor.get_Cipher()

            }
        }) {
            Text(text = "Ответить")
        }
    }
//    var users by remember { mutableStateOf(emptyList<QuestionEntity>()) }
//    LaunchedEffect(true) {
//        users = withContext(Dispatchers.IO) {
//            vm.quotes.first() // coroutine function to retrieve rows
//        }
//    }
//    LazyColumn {
//        items(users.size) {
//            Text(text = it.toString())
//        }
//    }
}
/*TODO(LazyColumn and coroutines viewmodel fetch data from DAO)*/

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TriviaTheme {
    }
}

