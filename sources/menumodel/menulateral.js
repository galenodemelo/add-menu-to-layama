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
