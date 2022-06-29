namespace Xamarin_Android_Intent_Digital_Hub.IntentServices.Sat
{
    class AssociarAssinatura : SatCommand
    {
        readonly int numSessao;
        readonly string codAtivacao;
        readonly string cnpjSH;
        readonly string assinaturaAC;

        public AssociarAssinatura(int numSessao, string codAtivacao, string cnpjSH, string assinaturaAC) : base("AssociarAssinatura")
        {
            this.numSessao = numSessao;
            this.codAtivacao = codAtivacao;
            this.cnpjSH = cnpjSH;
            this.assinaturaAC = assinaturaAC;
        }

        protected override string FunctionParameters()
        {
            return "\"numSessao\"" + ":" + numSessao + "," +
                    "\"codAtivacao\"" + ":" + "\"" + codAtivacao + "\"" + "," +
                    "\"cnpjSH\"" + ":" + "\"" + cnpjSH + "\"" + "," +
                    "\"assinaturaAC\"" + ":" + "\"" + assinaturaAC + "\"";
        }
    }
}