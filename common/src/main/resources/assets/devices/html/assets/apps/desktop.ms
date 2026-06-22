def renderDesktop(gfx) {
    gfx.clear(51, 51, 51, 255)
}

def renderTaskbar(gfx) {
    gfx.clear(0, 0, 0, 128)
    gfx.drawString("Taskbar", 60, 10)
}

def startMenuOpen() {
    let files = sys.fs.list("usr/share/applications")
    let menu = sys.createWindow("Start Menu", 0, sys.screenHeight - 34 - 30 * files.length - 10, 200, 30 * files.length + 10)
    menu.setDecorated(0)
    menu.setTopMost(1)
    let i = 0
    let y = 5
    while i != files.length {
        let filename = files.get(i)
        let content = sys.fs.read("usr/share/applications/" + filename)
        let app = sys.parseJson(content)
        let btn = menu.addWidget("button")
        btn.setText(app.name)
        btn.setScript(app.script)
        btn.setPosition(5, y)
        btn.setSize(190, 25)
        let i = i + 1
        let y = y + 30
    }
}

let desktop = sys.createWindow("Desktop", 0, 0, sys.screenWidth, sys.screenHeight)
desktop.setFocusable(1)
desktop.setBottomMost(1)
desktop.setDecorated(0)
desktop.setContentRenderer(renderDesktop)

let taskbar = sys.createWindow("Taskbar", 0, sys.screenHeight - 34, sys.screenWidth, 34)
taskbar.setFocusable(1)
taskbar.setTopMost(1)
taskbar.setDecorated(0)
taskbar.setTransparent(1)
taskbar.setContentRenderer(renderTaskbar)

let startBtn = taskbar.addWidget("button")
startBtn.setText("Start")
startBtn.setPosition(0, 0)
startBtn.setSize(50, 34)
startBtn.onButton(startMenuOpen)
