export GPR_USER
export GPR_KEY
export JAVA_HOME

OPENSBI_VERSION = 1.8.1
OPENSBI_REPO    = riscv-software-src/opensbi
OPENSBI_URL     = https://github.com/$(OPENSBI_REPO)/releases/download/v$(OPENSBI_VERSION)/opensbi-$(OPENSBI_VERSION)-rv-bin.tar.xz
OPENSBI_HASH    = 43d0ae79e58355b267c58dca056cf5816c70e71dfa2a67aaa54da3a6b590e49d127d83451b70d97b034998f7267c83b1d50fb7971753a7e0e34fd3a9ac7c159b

firmwares       = fw_dynamic fw_jump
fw_jump.hash    = a1154a6915cb4e4beb8dbd10e7e0edd90a743db004f7bc8c03dd208d00fc99e377c1818039df50912bdbcdd670f615c7b2f220d6b7b74d2e881b04ed3d9f483e
fw_dynamic.hash = 0b7449e159f2a10fdeebea6a418d9b941d18b20dfa3940ea42ac7493c0fb0464786ab9b5ccf035171d95e26ed439186d6cdec57746cec43cdef6f9015d3ea673

tarball=opensbi.tar.xz

firmware_srcdir=opensbi-$(OPENSBI_VERSION)-rv-bin/share/opensbi/lp64/generic/firmware
firmware_outdir=src/main/resources/assets/ocsedna/binary

all: gradle

gradle: fw_bin_relocate
	gradle build

clean:
	rm "$(firmware_outdir)/fw_dynamic.bin" \
		"$(firmware_outdir)/fw_jump.bin" \
		"$(tarball)"

# Pipeline to download and check sha512sums of files.
$(tarball):
	curl -L $(OPENSBI_URL) -o $@

fw_tar_extract: $(tarball)
	tar xvf $(tarball) --wildcards "$(firmware_srcdir)/*.bin"

fw_tar_checksum: fw_tar_extract
	echo "$(OPENSBI_HASH) $(tarball)" | sha512sum -c

$(firmwares): fw_tar_checksum
	echo "$($@.hash) $(firmware_srcdir)/$@.bin" | sha512sum -c

fw_bin_relocate: $(firmwares)
	mv "$(firmware_srcdir)"/*.bin "$(firmware_outdir)"
	rm -r "opensbi-$(OPENSBI_VERSION)-rv-bin/"
