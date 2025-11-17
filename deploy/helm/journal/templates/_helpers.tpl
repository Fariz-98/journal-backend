{{/* Base name for objects */}}
{{- define "journal.fullname" -}}
{{- if .Values.nameOverride -}}
{{- .Values.nameOverride | trunc 63 | trimSuffix "-" -}}
{{- else -}}
{{- .Release.Name | trunc 63 | trimSuffix "-" -}}
{{- end -}}
{{- end -}}

{{/* consistent labeling */}}
{{- define "journal.labels" -}}
app.kubernetes.io/name: {{ include "journal.fullname" .}}
app.kubernetes.io/instance: {{ .Release.Name }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
app.kubernetes.io/part-of: journal-local
app.kubernetes.io/managed-by: {{ .Release.Service }}
{{- end }}