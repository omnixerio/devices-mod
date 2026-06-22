def renderBackground(gfx) {
    gfx.clear(128, 178, 230, 255)
}

def renderWelcome(gfx) {
    gfx.clear(38, 38, 38, 255)
}

def onClosePrevent() {
    return 0
}

def onFinishClick() {
    welcome.close()
    background.close()
    sys.launchApp("DesktopApp")
}

def onBackgroundResize(win, w, h) {
    win.setSize(w, h)
}

def onWelcomeResize(win, w, h) {
    win.setSize(w - 100, h - 100)
    button.setPosition(30, 30)
}

let sw = sys.screenWidth
let sh = sys.screenHeight

let background = sys.createWindow("Background", 0, 0, sw, sh)
background.setCloseCallback(onClosePrevent)
background.setScreenResizeCallback(onBackgroundResize)
background.setMovable(0)
background.setResizable(0)
background.setFocusable(0)
background.setBottomMost(1)
background.setContentRenderer(renderBackground)

let welcome = sys.createWindow("Welcome to MineOS", 50, 50, sw - 100, sh - 100)
welcome.setCloseCallback(onClosePrevent)
welcome.setMovable(0)
welcome.setResizable(0)
welcome.setScreenResizeCallback(onWelcomeResize)
welcome.setContentRenderer(renderWelcome)

let button = welcome.addWidget("button")
button.setText("Finish")
button.setPosition(welcome.width - 100, welcome.height - 30)
button.setSize(90, 20)
button.onButton(onFinishClick)
