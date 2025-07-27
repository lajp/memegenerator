{
  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";
    systems.url = "github:nix-systems/default";
    sbt.url = "github:zaninime/sbt-derivation";
    sbt.inputs.nixpkgs.follows = "nixpkgs";
  };

  outputs =
    {
      self,
      nixpkgs,
      sbt,
      systems,
      ...
    }@inputs:
    let
      forEachSystem = nixpkgs.lib.genAttrs (import systems);
    in
    {
      packages = forEachSystem (
        system:
        let
          pkgs = nixpkgs.legacyPackages.${system};
        in
        {
          default = sbt.mkSbtDerivation.${system} rec {
            pname = "memegenerator-bot";
            version = "1.0.0";
            src = ./.;

            buildInputs = with pkgs; [
              pkgs.sbt
              jdk17_headless
              makeWrapper
            ];

            depsSha256 = "sha256-IXKADlGvsQgErbZCeB/2rHvGb3nuXZ0qTUaTGJhjfS0=";

            LANG = "C.UTF-8";

            buildPhase = ''
              sbt assembly
            '';

            installPhase = ''
              mkdir -p $out/bin
              mkdir -p $out/share/java

              cp target/scala-3.3.3/*.jar $out/share/java
              cp -r assets $out/share

              makeWrapper "${pkgs.jdk17}/bin/java" $out/bin/${pname} \
                --add-flags "-cp $out/share/java/*.jar s1.telegrambots.YourBot" \
                --set-default ASSETS_PATH $out/share/assets
            '';
          };

          docker = pkgs.dockerTools.buildLayeredImage {
            name = "ghcr.io/lajp/memegenerator-bot";
            tag = "latest";

            config.Cmd = [ "${self.outputs.packages.${system}.default}/bin/memegenerator-bot" ];
          };
        }
      );

      devShells = forEachSystem (
        system:
        let
          pkgs = nixpkgs.legacyPackages.${system};
        in
        {
          default = pkgs.mkShell {
            nativeBuildInputs = with pkgs; [
              pkgs.sbt
              jdk17_headless
            ];
          };
        }
      );
    };
}
