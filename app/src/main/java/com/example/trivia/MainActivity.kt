package com.example.trivia

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.compose.TriviaTheme
import com.example.compose.md_theme_dark_onError
import com.example.compose.md_theme_dark_outline
import com.example.compose.md_theme_dark_tertiary
import com.example.trivia.data.*
import com.example.trivia.database.QuestionFirebase
import com.example.trivia.database.QuestionTrivia
import com.example.trivia.game.Processor
import com.example.trivia.game.TriviaProcessor
import com.example.trivia.ui.theme.*
import com.example.trivia.util.LoadingState
import com.example.trivia.util.TriviaApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
        val tViewModel=TriviaViewModel()
        val triviaProcessor=TriviaProcessor()
        setContent {
            TriviaTheme {

                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    TriviaApp(modifier = Modifier,viewModelFirebase,processor,tViewModel,triviaProcessor)
                }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarComposable(text:String,function:()->Unit,descrition: String)
{
    TopAppBar(
        title = {
            Text(text = text)
        },
        navigationIcon = {
            IconButton(onClick = {function()}) {
                Icon(Icons.Filled.ArrowBack, "backIcon")
            }
        },
        actions = {
            var showDialog by remember { mutableStateOf(false) }
            IconButton(onClick = {showDialog = true }) {
                Icon(
                    imageVector = Icons.Rounded.Info,
                    contentDescription = null,
                )
            }
            if (showDialog)
                MyDialog(onDismiss = {showDialog=false},descrition )
        }
    )
}
@Composable
fun MyDialog(onDismiss: () -> Unit,descrition:String) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Как играть?") },
        text = { Text(text = descrition) },
        confirmButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(text = "Все понял!")
            }
        })
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MillionaireLayout(tvm:TriviaViewModel,triviaProcessor: TriviaProcessor,goBackClicked: () -> Unit) {
    var progress by remember { mutableStateOf(0.0f) }
    val baseColorF= remember {
        mutableStateOf(md_theme_dark_outline)
    }
    val variants= remember {
        mutableStateOf(triviaProcessor.allVariants)
    }
    val baseColorS= remember {
        mutableStateOf(md_theme_dark_outline)
    }
    var baseColorT= remember {
        mutableStateOf(md_theme_dark_outline)
    }
    val baseColorFf= remember {
        mutableStateOf(md_theme_dark_outline)
    }
    val data = remember { mutableStateOf<QuestionTrivia>(
        QuestionTrivia(" ", " ",
            listOf(" "," "," "," ")
        )
    ) }
    val composableScope = rememberCoroutineScope()
    val scope = rememberCoroutineScope()
    LaunchedEffect(true)
    {
        tvm.ReloadEntity()
        data.value=tvm.getNext()
        data.value.incorrectAnswers=data.value.incorrectAnswers.shuffled().shuffled()
        triviaProcessor.answer=data.value.answer
        triviaProcessor.allVariants=data.value.incorrectAnswers.toMutableList()
        variants.value=triviaProcessor.allVariants
    }
    composableScope.launch {
        val result=TriviaApi.retrofitService.getQuestions()
        print(result.results.map { it.question })
    }
    suspend fun update()
    {
        data.value=tvm.getNext()
        data.value.incorrectAnswers=data.value.incorrectAnswers.shuffled().shuffled()
        triviaProcessor.answer=data.value.answer
        triviaProcessor.allVariants=data.value.incorrectAnswers.toMutableList()
        variants.value=triviaProcessor.allVariants
        baseColorF.value= md_theme_dark_outline
        baseColorS.value=md_theme_dark_outline
        baseColorT.value=md_theme_dark_outline
        baseColorFf.value=md_theme_dark_outline
        progress = 0.0f
    }
    Scaffold(topBar = {
        TopAppBarComposable(text = "\"Кто хочет стать миллионером?\"",goBackClicked, stringResource(
            id = R.string.descriptionMilly
        )) }) {


        Column(
            modifier = Modifier.padding(75 .dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = data.value.question,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Текущий счет ${triviaProcessor.score}",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                textAlign = TextAlign.Center
            )
            Row(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(10.dp)
                ) {
                    Button(
                        onClick = {
                            if (triviaProcessor.isRight(variants.value[0])) {
                                baseColorF.value = md_theme_dark_tertiary

                            } else {
                                baseColorF.value = md_theme_dark_onError
                            };scope.launch { delay(1000L);update() }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = baseColorF.value)
                    ) {
                        Box {
                            Text(text = variants.value[0], modifier = Modifier.align(Alignment.Center))
                        }
                    }
                    Button(
                        onClick = {
                            if (triviaProcessor.isRight(variants.value[1])) {
                                baseColorS.value = md_theme_dark_tertiary

                            } else {
                                baseColorS.value = md_theme_dark_onError
                            };scope.launch { delay(1000L);update() }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = baseColorS.value)
                    ) {
                        Box {
                            Text(text = variants.value[1], modifier = Modifier.align(Alignment.Center))
                        }
                    }
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(10.dp)
                ) {
                    Button(
                        onClick = {
                            if (triviaProcessor.isRight(variants.value[2])) {
                                baseColorT.value = md_theme_dark_tertiary

                            } else {
                                baseColorT.value = md_theme_dark_onError
                            };scope.launch { delay(1000L);update() }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = baseColorT.value)
                    ) {
                        Box {
                            Text(text = variants.value[2], modifier = Modifier.align(Alignment.Center))
                        }
                    }
                    Button(
                        onClick = {
                            if (triviaProcessor.isRight(variants.value[3])) {
                                baseColorFf.value = md_theme_dark_tertiary

                            } else {
                                baseColorFf.value = md_theme_dark_onError
                            };scope.launch { delay(1000L);update() }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = baseColorFf.value)
                    ) {
                        Box {
                            Text(text = variants.value[3], modifier = Modifier.align(Alignment.Center))
                        }
                    }
                }

            }
            Spacer(modifier = Modifier.height(16.dp))
            LinearProgressIndicator(progress)
            LaunchedEffect(Unit) {
                while (true) {
                    delay(1000L)
                    progress += 0.01f
                    if (progress >= 1.0f) {
                        progress = 0.0f
                    }
                }
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { triviaProcessor.getHint() }) {
                    Text(text = "Взять подсказку")

                }
                Text(
                    text = "Вы можете воспользоваться подсказкой, только если Вы угадали подряд 3 ответа.Прогресс - ${triviaProcessor.successRow}/3",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center
                )
            }

        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
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
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
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
            label = { Text("Пароль") },
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
            label = { Text("Повторите пароль") },
            modifier = Modifier.padding(16.dp),
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation()
        )

        Button(
            onClick = { viewModel.signUp(email, password) },
            modifier = Modifier
                .padding(top = 16.dp, bottom = 16 .dp)
                .fillMaxWidth(),
            enabled = isFormValid,
            shape = RectangleShape
        ) {
            Text("Зарегистрироваться")
        }

        TextButton(
            onClick = { signInClicked() },
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Уже есть аккаунт? Перейдите к авторизации")

        }
    }
    when (state.status) {
        LoadingState.Status.SUCCESS -> {
            onNextButtonClicked()
        }
        LoadingState.Status.FAILED -> {
            Toast.makeText(LocalContext.current,state.msg ?: "Возникла ошибка",Toast.LENGTH_LONG).show()
        }
        else -> {}
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TriviaApp(modifier: Modifier, vm: FireBaseViewModel, proc:Processor,tvm: TriviaViewModel,triviaProcessor: TriviaProcessor) {
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
                Greeting(modifier=Modifier,{navController.navigate("milly")}) { navController.navigate("game") }
            }
            composable(route="game")
            {
                MyScreen(vm = vm, processor = proc ) {
                    navController.navigateUp()
                }
            }
            composable(route="milly")
            {
                MillionaireLayout(tvm,triviaProcessor,{navController.navigateUp()})
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun LoginScreen(viewModel: AuthViewModel,onNextButtonClicked: () -> Unit,signUPClicked : ()->Unit) {

    var userEmail by remember { mutableStateOf("") }
    var userPassword by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val state by viewModel.loadingState.collectAsState()

    Scaffold(
        snackbarHost={SnackbarHost(hostState = snackbarHostState)},
        topBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                TopAppBar(
                    title = {
                        Text(text = "Вход")
                    },
                    navigationIcon = {
                        val activity = (LocalContext.current as? Activity)
                        IconButton(onClick = { activity?.finish() }) {
                            Icon(
                                imageVector = Icons.Rounded.ArrowBack,
                                contentDescription = null,
                            )
                        }
                    },
                )
            }
        },
        content = {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 100.dp),
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
                            Text(text = "Пароль")
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
                            Text(text = "Войти")
                        },
                        onClick = {
                            viewModel.signInWithEmailAndPassword(
                                userEmail.trim(),
                                userPassword.trim()
                            )
                        },
                        shape = RectangleShape
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                    TextButton(
                        onClick = { signUPClicked() },
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text("Еще нет аккаунта?")

                    }



                    when (state.status) {
                        LoadingState.Status.SUCCESS -> {
                            onNextButtonClicked()
                        }
                        LoadingState.Status.VERIFICATION_WAIT-> {
                            Toast.makeText(LocalContext.current,"Подтвердите регистрацию на почте",Toast.LENGTH_LONG).show()
                        }
                        LoadingState.Status.FAILED -> {
                            Toast.makeText(LocalContext.current,state.msg ?: "Error",Toast.LENGTH_LONG).show()
                        }
                        else -> {}
                    }
                }
            )
        })
}


@Composable
fun Greeting(modifier: Modifier = Modifier, onMillyButtonClicked: () -> Unit, onTriviaButtonClicked: () -> Unit) {
    var backPressedCount by remember { mutableStateOf(0) }
    BackHandler(enabled = true, onBack = {backPressedCount+=1})
    if (backPressedCount>=2)
    {
        val activity = (LocalContext.current as? Activity)
        activity?.finish()
    }
    Column(
        modifier.fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(text = "Викторина", fontWeight = FontWeight.Bold)
        Image(
            painter = painterResource(id = R.drawable.clover_svgrepo_com),
            contentDescription = null,
            modifier = modifier
                .padding(20.dp)
                .size(100.dp)
        )
        Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier.fillMaxHeight(1f)) {
            Button(
                onClick = { onMillyButtonClicked() }, modifier = modifier
                    .weight(1f)
                    .padding(10.dp)
            )
            {
                Text("Кто хочет стать миллионером?")
            }
            Button(
                onClick = { onTriviaButtonClicked() }, modifier = modifier
                    .weight(1f)
                    .padding(10.dp)
            ) {
                Text(text = "Викторина")

            }
        }

    }

}
@Composable
fun TestMoshi()
{
    //TODO//
}
@Composable
fun MyComposable(vm: MyViewModel) {
    val context = LocalContext.current
    var ready = false
    vm.getIsDatabaseCreated().observe(LocalLifecycleOwner.current) { isCreated ->
        ready = isCreated
    }
    if (!ready) {

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        val myDao = vm.db.QuestionsDAO()
        val data = myDao.getQuestion().value

        if (data != null) {
            Text(text = data.answer)
        }
    }
}




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyScreen(
    modifier: Modifier = Modifier, vm: FireBaseViewModel, processor: Processor,
    onBackButtonClicked: () -> Unit) {
    val focusManager = LocalFocusManager.current
    val text = remember { mutableStateOf("") }
    var progress by remember { mutableStateOf(0.0f) }
    var textFieldValue by remember { mutableStateOf("") }
    val pair = remember { mutableStateOf<QuestionFirebase>(QuestionFirebase("aboba", "aboba")) }
    var isVisible= false
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
    Scaffold(topBar = { TopAppBarComposable(text = "Викторина",onBackButtonClicked, stringResource(id = R.string.descriptionTrivia))}) {


        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(70.dp), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = processor.question,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 8
            )


            Text(
                text = if (text.value == "") processor.get_Cipher() else text.value,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Текущий счет ${processor.score}",
                fontSize = 15.sp,
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
            if (isVisible) Text(text="Неверный ответ",fontSize = 15.sp, fontWeight = FontWeight.Bold)
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
            Button(onClick = { update();progress = 0.0f;processor.erase_counter();isVisible=false }) {
                Text(text = "Следующий вопрос")
            }
            Button(onClick = {
                processor.tryAnswer = textFieldValue
                if (processor.isAnswerRight()) {
                    update()
                    isVisible=false
                    progress = 0.0f
                    text.value = processor.get_Cipher()

                }
                else
                    isVisible=true
            }) {
                Text(text = "Ответить")
            }
        }
    }
//
}
/*TODO(LazyColumn and coroutines viewmodel fetch data from DAO)*/

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
//        val factory = ViewModelFactory(this.application)
    val viewModelFirebase = FireBaseViewModel()
    viewModelFirebase.go()
//        val viewModel = factory.let { ViewModelProvider(this, it)[MyViewModel::class.java] }
    val processor = Processor()
    val tViewModel=TriviaViewModel()
    val triviaProcessor=TriviaProcessor()
        TriviaTheme {

            // A surface container using the 'background' color from the theme
            Surface(
                modifier = Modifier.fillMaxSize()
            ) {
                TriviaApp(modifier = Modifier,viewModelFirebase,processor,tViewModel,triviaProcessor)
            }
    }
}

