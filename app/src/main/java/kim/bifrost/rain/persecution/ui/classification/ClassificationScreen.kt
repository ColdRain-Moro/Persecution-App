package kim.bifrost.rain.persecution.ui.classification

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.rounded.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.rememberImagePainter
import coil.size.Scale
import coil.transform.CircleCropTransformation
import kim.bifrost.rain.persecution.R
import kim.bifrost.rain.persecution.model.bean.ClassificationData
import kim.bifrost.rain.persecution.ui.Screen
import kim.bifrost.rain.persecution.ui.theme.gray
import kim.bifrost.rain.persecution.ui.theme.titleTextStyle
import kim.bifrost.rain.persecution.ui.widgets.SwipePagingList
import kim.bifrost.rain.persecution.utils.getOrNull
import kotlinx.coroutines.launch

/**
 * kim.bifrost.rain.persecution.ui.classification.ClassificationScreen
 * Persecution
 *
 * @author ??????
 * @since 2022/3/11 17:49
 **/
@Preview
@Composable
fun ClassificationScreen(navController: NavController = rememberNavController()) {
    val viewModel = viewModel<ClassificationViewModel>()
    val items = viewModel.pagingData.collectAsLazyPagingItems()
    var showDialog by remember { mutableStateOf(false) }
    var newClassName by remember { mutableStateOf("") }
    var newClassDescription by remember { mutableStateOf("") }
    // ??????uri????????????????????????url
    var newClassAvatar by remember { mutableStateOf<Uri?>(null) }
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()
    // ????????????
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        coroutineScope.launch {
            if (uri == null) {
                scaffoldState.snackbarHostState.showSnackbar("??????????????????")
                return@launch
            }
            newClassAvatar = uri
        }
    }
    Scaffold(
        scaffoldState = scaffoldState,
        modifier = Modifier.fillMaxSize(),
        snackbarHost = {
            SnackbarHost(hostState = it)
        },
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.padding(bottom = 50.dp),
                onClick = { showDialog = true },
                backgroundColor = Color.White,
                contentColor = Color.Gray
            ) {
                Icon(Icons.Outlined.Add, contentDescription = null)
            }
        }
    ) {
        SwipePagingList(
            items = items,
            header = {
                Text(
                    text = "??????",
                    style = titleTextStyle,
                    modifier = Modifier.padding(vertical = 10.dp)
                )
            },
            content = {
                items(items.itemCount / 2 + 1) { s ->
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row {
                            val item = items.getOrNull(s * 2)
                            val item2 = items.getOrNull(s * 2 + 1)
                            item?.let {
                                ClassificationItem(
                                    modifier = Modifier.weight(1f),
                                    data = item,
                                    onClick = {
                                        navController.navigate(Screen.Classification.generateRoute("id" to item.id))
                                    }
                                )
                            }
                            item2?.let {
                                ClassificationItem(
                                    modifier = Modifier.weight(1f),
                                    data = item2,
                                    onClick = {
                                        navController.navigate(Screen.Classification.generateRoute("id" to item2.id))
                                    }
                                )
                            }
                        }
                    }
                }
            }
        )
        if (showDialog) {
            Dialog(
                onDismissRequest = { showDialog = false }
            ) {
                Column(
                    modifier = Modifier
                        .wrapContentSize()
                        .clip(RoundedCornerShape(10))
                        .background(Color.White)
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "???????????????",
                        style = TextStyle(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 24.sp,
                            letterSpacing = 0.25.sp
                        )
                    )

                    val img = if (newClassAvatar == null) R.drawable.noface else newClassAvatar

                    Image(
                        modifier = Modifier
                            .padding(vertical = 10.dp)
                            .size(80.dp)
                            .pointerInput(Unit) { detectTapGestures(onTap = { launcher.launch(arrayOf("image/*")) }) },
                        painter =  rememberImagePainter(img) {
                            crossfade(true)
                            transformations(CircleCropTransformation())
                        },
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )

                    OutlinedTextField(
                        modifier = Modifier
                            .padding(vertical = 5.dp),
                        value = newClassName,
                        onValueChange = { newClassName = it },
                        singleLine = true,
                        label = { Text(text = "????????????") },
                        textStyle = TextStyle(
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                    )

                    OutlinedTextField(
                        modifier = Modifier
                            .padding(vertical = 5.dp),
                        value = newClassDescription,
                        onValueChange = { newClassDescription = it },
                        singleLine = false,
                        maxLines = 3,
                        label = { Text(text = "????????????") },
                        textStyle = TextStyle(
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                    )

                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                if (newClassAvatar == null) {
                                    scaffoldState.snackbarHostState.showSnackbar("???????????????!")
                                    return@launch
                                }
                                if (newClassName.isEmpty()) {
                                    scaffoldState.snackbarHostState.showSnackbar("????????????????????????")
                                    return@launch
                                }
                                showDialog = false
                                val res = viewModel.createNewClassification(newClassAvatar!!, newClassName, newClassDescription)
                                if (res.errorCode != 0) {
                                    scaffoldState.snackbarHostState.showSnackbar(res.message)
                                } else {
                                    scaffoldState.snackbarHostState.showSnackbar("????????????!")
                                }
                            }
                        },
                        modifier = Modifier.padding(top = 10.dp)
                    ) {
                        Icon(Icons.Rounded.Add, contentDescription = null)
                    }
                }
            }
        }
    }
}

@Composable
fun ClassificationItem(
    modifier: Modifier = Modifier,
    data: ClassificationData = ClassificationData(
        2,
        "??????",
        "https://gitee.com/coldrain-moro/images_bed/raw/master/images/chino.jpg",
        "??????????????????"
    ),
    onClick: () -> Unit = {}
) {
    Box(
        modifier.pointerInput(Unit) {
            detectTapGestures(
                onTap = {
                    onClick()
                }
            )
        }
    ) {
        Card(
            modifier = Modifier
                .padding(10.dp)
                .width(140.dp)
                .height(75.dp)
                .align(Alignment.BottomCenter),
            shape = RoundedCornerShape(10.dp)
        ) {
            Column(Modifier.align(Alignment.Center)) {
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .widthIn(max = 130.dp),
                    text = data.name,
                    style = TextStyle(
                        fontSize = 18.sp
                    ),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
                Text(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .widthIn(max = 130.dp),
                    text = data.description,
                    style = TextStyle(
                        fontSize = 18.sp,
                        color = Color.Gray
                    ),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
            }
        }
        Card(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(bottom = 70.dp)
                .size(130.dp)
        ) {
            Image(
                modifier = Modifier
                    .size(150.dp)
                    .align(Alignment.TopCenter),
                painter = rememberImagePainter(
                    data.avatar,
                    builder = {
                        placeholder(R.drawable.loading)
                        crossfade(true)
                        scale(Scale.FIT)
                    }
                ),
                contentDescription = null,
            )
        }
    }
}

@Preview
@Composable
fun Test() {
    Box(modifier = Modifier.fillMaxSize()) {
        ClassificationItem()
    }
}