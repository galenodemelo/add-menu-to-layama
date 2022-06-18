var abriu = false
var imagemDoBotao = document.getElementById("imgButton")
var intervaloAtualizaBotaoMais = setInterval(function () { imagemDoBotao.src = "./menu/img/button_01.gif" }, 5000)
var navegacaoLateral = document.getElementById("sidenav")

function openNav() {
    if (!abriu) {
        navegacaoLateral.style.margin = "0"
        imagemDoBotao.src = "./menu/img/button_02.gif"

        abriu = true
        clearInterval(intervaloAtualizaBotaoMais)
    } else {
        navegacaoLateral.style.margin = "0 -320px 0 0"
        imagemDoBotao.src = "./menu/img/button_01.gif"

        abriu = false
    }
}

/**
 * Altera a câmera ao clicar no botão do menu
 */
 function refreshCamera() {
    if (abriu) openNav() // Fecha o menu se estiver aberto

    const camera = new URLSearchParams(window.location.search).get('camera')
    let cameraList = getLayamaCameras()
    cameraList.sort(function(x, y) { 
        return x.a == camera ? -1 : y.a == camera ? 1 : 0 
    })
    getLayamaCameras = function() { return cameraList }

    const scriptList = document.getElementsByTagName("script")
    for (let script of scriptList) {
        if (script.src.includes("menulateral.js")) continue
        if (!script.src.length) continue

        script.src = script.src.replace(/\?.*$/, "") + "?t=" + new Date().getTime()
    }
}

refreshCamera()