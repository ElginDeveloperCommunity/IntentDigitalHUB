namespace Xamarin_Android_Intent_Digital_Hub.IntentServices.Termica
{
    class AbreConexaoImpressora : TermicaCommand
    {
        readonly int tipo;
        readonly string modelo;
        readonly string conexao;
        readonly int parametro;

        public AbreConexaoImpressora(int tipo, string modelo, string conexao, int parametro) : base("AbreConexaoImpressora")
        {
            this.tipo = tipo;
            this.modelo = modelo;
            this.conexao = conexao;
            this.parametro = parametro;
        }

        protected override string FunctionParameters()
        {
            return "\"tipo\"" + ":" + tipo + "," +
                    "\"modelo\"" + ":" + "\"" + modelo + "\"" + "," +
                    "\"conexao\"" + ":" + "\"" + conexao + "\"" + "," +
                    "\"parametro\"" + ":" + parametro;
        }
    }
}